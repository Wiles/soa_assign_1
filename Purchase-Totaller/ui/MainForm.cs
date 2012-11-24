using Purchase_Totaller.hl7;
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

namespace Purchase_Totaller
{
    public partial class MainForm : Form
    {
        private ServiceConnection connection;
        private RemoteService queriedService;

        private string address;
        private int port;
        private string serviceTag;
        private string teamName;

        public MainForm()
        {
            InitializeComponent();
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            // TODO: Check if client is registered

        }

        private void connectToolStripMenuItem_Click(object sender, EventArgs e)
        {
            var form = new ConnectForm();
            if (form.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    address = form.Address;
                    port = form.Port;
                    serviceTag = form.ServiceTag;
                    teamName = form.TeamName;

                    connection = new ServiceConnection(teamName, IPAddress.Parse(address), port);
                    connection.Register();
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

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void runToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            // TODO: Check if connection is registered

            var call = new RemoteServiceCall(serviceTag, teamName, (int)connection.TeamId);

            int i = 1;
            foreach (DataGridViewRow row in argGrid.Rows)
            {
                var argName = row.Cells[0].Value.ToString();
                var argDataType= ServiceArgument.TypeFromString(row.Cells[1].Value.ToString());
                var argMandatory = bool.Parse(row.Cells[2].Value.ToString());
                var argValue = row.Cells[3].Value.ToString();

                var arg = new ServiceArgument(i++, argName, argDataType, argMandatory);
                arg.Value = argValue;
                call.Args.Add(arg);
            }

            i = 1;
            foreach (DataGridViewRow row in respGrid.Rows)
            {
                var respName = row.Cells[0].Value.ToString();
                var respDataType = ServiceArgument.TypeFromString(row.Cells[1].Value.ToString());
                var respValue = row.Cells[2].Value.ToString();

                var ret = new ServiceReturn(i++, respName, respDataType, respValue);
                call.Returns.Add(ret);
            }

            connection.ExecuteService(call);
        }
    }
}
