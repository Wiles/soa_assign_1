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

                        queriedService = connection.QueryService(serviceTag);
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

            // TODO: Parse datagrid and make call
            // call.Args.Add();

            connection.ExecuteService(call);
        }
    }
}
