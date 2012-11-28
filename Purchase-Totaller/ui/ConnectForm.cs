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
    public partial class ConnectForm : Form
    {
        public string Address;
        public string ServiceTag;
        public string TeamName;
        public int Port;

        public ConnectForm()
        {
            InitializeComponent();
        }

        private void ok_Click(object sender, EventArgs e)
        {
            int portNum = 0;
            if (String.IsNullOrWhiteSpace(address.Text))
            {
                MessageBox.Show("Please enter an address");
            }
            else if (String.IsNullOrWhiteSpace(serviceTag.Text))
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

        private void cancel_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }
    }
}
