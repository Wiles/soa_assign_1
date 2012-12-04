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
    public partial class UnRegisterForm : Form
    {
        /// <summary>
        /// Address of the registry
        /// </summary>
        public string Address;

        /// <summary>
        /// Team Id
        /// </summary>
        public int TeamId;

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
        public UnRegisterForm()
        {
            InitializeComponent();
        }

        /// <summary>
        /// Handle ok clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ok_Click(object sender, EventArgs e)
        {
            int teamId = 0;
            int portNum = 0;
            if (String.IsNullOrWhiteSpace(address.Text))
            {
                MessageBox.Show("Please enter an address");
            }
            else if (!int.TryParse(this.teamId.Text, out teamId))
            {
                MessageBox.Show("Please enter a numeric team id");
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
                this.TeamId = teamId;
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
