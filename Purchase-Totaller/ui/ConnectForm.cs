using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Hl7Lib
{
    /// <summary>
    /// Connect to the SOA Registry and a service
    /// </summary>
    public partial class ConnectForm : Form
    {
        /// <summary>
        /// Address of the registry
        /// </summary>
        public string Address;

        /// <summary>
        /// Service tag
        /// </summary>
        public string ServiceTag;

        /// <summary>
        /// Team name
        /// </summary>
        public string TeamName;

        /// <summary>
        /// Port of the registry
        /// </summary>
        public int Port;

        /// <summary>
        /// </summary>
        public ConnectForm(bool serviceTagEnabled = true)
        {
            InitializeComponent();
            serviceTag.Enabled = serviceTagEnabled;
        }

        /// <summary>
        /// Handle ok clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ok_Click(object sender, EventArgs e)
        {
            int portNum = 0;
            if (String.IsNullOrWhiteSpace(address.Text))
            {
                MessageBox.Show("Please enter an address");
            }
            else if (serviceTag.Enabled && String.IsNullOrWhiteSpace(serviceTag.Text))
            {
                MessageBox.Show("Please enter a service tag");
            }
            else if (String.IsNullOrWhiteSpace(team.Text))
            {
                MessageBox.Show("Please enter a team name");
            }
            else if (!int.TryParse(port.Text, out portNum))
            {
                MessageBox.Show("Please enter a valid port number");
            }
            else if (portNum < 0 || portNum > 65535)
            {
                MessageBox.Show("Please enter a port number between 0 and 65535");
            }
            else
            {
                this.Address = address.Text;
                this.ServiceTag = serviceTag.Text;
                this.Port = portNum;
                this.TeamName = team.Text;

                DialogResult = DialogResult.OK;
            }
        }

        /// <summary>
        /// Handle cancel clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void cancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }
    }
}
