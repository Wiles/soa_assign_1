using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Hl7Lib;
using SoaClient.ui;

namespace SoaClient
{
    /// <summary>
    /// The main form
    /// </summary>
    public partial class MainForm : Form
    {
        /// <summary>
        /// The connection we're connected to. Null if not connected
        /// </summary>
        private ServiceConnection connection;

        /// <summary>
        /// The service we're connected to. Null if not connected
        /// </summary>
        private RemoteService queriedService;

        /// <summary>
        /// Address of the registry
        /// </summary>
        private string address;

        /// <summary>
        /// Port of the registry
        /// </summary>
        private int port;

        /// <summary>
        /// Service tag
        /// </summary>
        private string serviceTag;

        /// <summary>
        /// Team name of the client
        /// </summary>
        private string teamName;

        /// <summary>
        /// Team id of the client
        /// </summary>
        private int teamId;

        public MainForm()
        {
            InitializeComponent();
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
        }

        /// <summary>
        /// Connect to the registry
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void connectToolStripMenuItem_Click(object sender, EventArgs e)
        {
            var form = new ConnectForm();
            if (form.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    argGrid.Rows.Clear();
                    respGrid.Rows.Clear();

                    address = form.Address;
                    port = form.Port;
                    serviceTag = form.ServiceTag;
                    teamName = form.TeamName;

                    connection = new ServiceConnection(Program.Logger, teamName, IPAddress.Parse(address), port);
                    var registerResponse = connection.Register();
                    teamId = registerResponse.TeamId;
                    if (connection.IsRegistered())
                    {
                        runToolStripMenuItem1.Enabled = true;

                        queriedService = connection.QueryService(serviceTag).Service;

                        var args = from a in queriedService.Args
                                   orderby a.Position
                                   select a;

                        foreach (var arg in args)
                        {
                            argGrid.Rows.Add(new object[] { arg.Name, ServiceArgument.TypeToString(arg.DataType), arg.Mandatory, arg.Value });
                        }

                        var resps = from r in queriedService.Returns
                                    orderby r.Position
                                    select r;
                        foreach (var resp in resps)
                        {
                            respGrid.Rows.Add(new object[] { resp.Name, ServiceArgument.TypeToString(resp.DataType), resp.Value });
                        }

                        toolStripStatusLabel.Text = String.Format("Service IP: {0}, Port: {1}, ServiceName: {2}, Description: {3}", 
                            queriedService.Ip.ToString(), queriedService.Port, queriedService.Name, queriedService.Description);
                    }
                    else
                    {
                        MessageBox.Show("Failure to register with registry");
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Failure to register with registry, because: " + ex.Message);
                }
            }
        }

        /// <summary>
        /// Exit program
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        /// <summary>
        /// Execute the service
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void runToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            try
            {
                // Re-register the client every time
                connection.Register();

                var ip = queriedService.Ip;
                var port = queriedService.Port;

                try
                {
                    queriedService = connection.QueryService(serviceTag).Service;
                }
                catch (Exception)
                {
                    throw new Exception("Service is no longer registered");
                }

                var serviceConnection = new ServiceConnection(Program.Logger, teamName, ip, port, false, connection.TeamId);

                var call = new RemoteServiceCall(queriedService.Name, teamName, (int)connection.TeamId);

                int i = 1;
                foreach (DataGridViewRow row in argGrid.Rows)
                {
                    var argName = row.Cells[0].Value.ToString();
                    var argDataType = ServiceArgument.TypeFromString(row.Cells[1].Value.ToString());
                    var argMandatory = bool.Parse(row.Cells[2].Value.ToString());
                    var argValue = row.Cells[3].Value.ToString();

                    try
                    {
                        switch (argDataType)
                        {
                            case ServiceDataType.Tint:
                                int.Parse(argValue);
                                break;
                            case ServiceDataType.Tdouble:
                                double.Parse(argValue);
                                break;
                            case ServiceDataType.Tfloat:
                                float.Parse(argValue);
                                break;
                            case ServiceDataType.Tchar:
                                if (argValue.Length > 1)
                                {
                                    throw new FormatException("Char field must be 1 character");
                                }
                                break;
                            case ServiceDataType.Tshort:
                                short.Parse(argValue);
                                break;
                            case ServiceDataType.Tlong:
                                long.Parse(argValue);
                                break;
                            default:
                                break;
                        }
                    }
                    catch (Exception)
                    {
                        throw new FormatException("Please enter a proper value for: " + argName);
                    }

                    if (argMandatory)
                    {
                        if (String.IsNullOrWhiteSpace(argValue))
                        {
                            throw new FormatException("Please enter a value for: " + argName);
                        }
                    }

                    var arg = new ServiceArgument(i++, argName, argDataType, argMandatory);
                    arg.Value = argValue;
                    call.Args.Add(arg);
                }

                i = 1;
                foreach (DataGridViewRow row in respGrid.Rows)
                {
                    var respName = row.Cells[0].Value.ToString();
                    var respDataType = ServiceArgument.TypeFromString(row.Cells[1].Value.ToString());
                    var respValue = "";

                    var ret = new ServiceReturn(i++, respName, respDataType, respValue);
                    call.Returns.Add(ret);
                }

                var executeResponse = serviceConnection.ExecuteService(call);
                foreach (var ret in executeResponse.Returned.Returns)
                {
                    foreach (DataGridViewRow row in respGrid.Rows)
                    {
                        var respName = row.Cells[0].Value.ToString();
                        if (respName == ret.Name)
                        {
                            var respDataType = ServiceArgument.TypeFromString(row.Cells[1].Value.ToString());
                            row.Cells[2].Value = ret.Value;
                            
                            break;
                        }
                    }
                    
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error executing service, because: " + ex.Message);
            }
        }

        /// <summary>
        /// Unregister anything
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void unRegisterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                var form = new ConnectForm(false);

                if (form.ShowDialog() == DialogResult.OK)
                {
                    var address = IPAddress.Parse(form.Address);
                    var port = form.Port;
                    var teamName = form.TeamName;

                    // unregister
                    var service = new ServiceConnection(Program.Logger, teamName, address, port);
                    service.Register();

                    service.UnRegister();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error unregistering, because: " + ex.Message);
            }
        }

        /// <summary>
        /// Show the about dialog
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                var form = new AboutBox();
                form.Show();
            }
            catch (Exception)
            {
                MessageBox.Show("Failure showing about box");
            }
        }
    }
}
