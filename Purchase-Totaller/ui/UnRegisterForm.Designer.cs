namespace Hl7Lib
{
    partial class UnRegisterForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.descLabel = new System.Windows.Forms.Label();
            this.addressLabel = new System.Windows.Forms.Label();
            this.address = new System.Windows.Forms.TextBox();
            this.port = new System.Windows.Forms.TextBox();
            this.portLabel = new System.Windows.Forms.Label();
            this.ok = new System.Windows.Forms.Button();
            this.cancel = new System.Windows.Forms.Button();
            this.teamId = new System.Windows.Forms.TextBox();
            this.serviceTagLabel = new System.Windows.Forms.Label();
            this.team = new System.Windows.Forms.TextBox();
            this.teamLabel = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // descLabel
            // 
            this.descLabel.AutoSize = true;
            this.descLabel.Location = new System.Drawing.Point(9, 7);
            this.descLabel.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.descLabel.Name = "descLabel";
            this.descLabel.Size = new System.Drawing.Size(90, 13);
            this.descLabel.TabIndex = 0;
            this.descLabel.Text = "UnRegister Team";
            // 
            // addressLabel
            // 
            this.addressLabel.AutoSize = true;
            this.addressLabel.Location = new System.Drawing.Point(9, 45);
            this.addressLabel.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.addressLabel.Name = "addressLabel";
            this.addressLabel.Size = new System.Drawing.Size(45, 13);
            this.addressLabel.TabIndex = 1;
            this.addressLabel.Text = "Address";
            // 
            // address
            // 
            this.address.Location = new System.Drawing.Point(77, 45);
            this.address.Margin = new System.Windows.Forms.Padding(2);
            this.address.Name = "address";
            this.address.Size = new System.Drawing.Size(219, 20);
            this.address.TabIndex = 2;
            // 
            // port
            // 
            this.port.Location = new System.Drawing.Point(77, 67);
            this.port.Margin = new System.Windows.Forms.Padding(2);
            this.port.Name = "port";
            this.port.Size = new System.Drawing.Size(60, 20);
            this.port.TabIndex = 3;
            // 
            // portLabel
            // 
            this.portLabel.AutoSize = true;
            this.portLabel.Location = new System.Drawing.Point(9, 70);
            this.portLabel.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.portLabel.Name = "portLabel";
            this.portLabel.Size = new System.Drawing.Size(26, 13);
            this.portLabel.TabIndex = 4;
            this.portLabel.Text = "Port";
            // 
            // ok
            // 
            this.ok.Location = new System.Drawing.Point(134, 141);
            this.ok.Margin = new System.Windows.Forms.Padding(2);
            this.ok.Name = "ok";
            this.ok.Size = new System.Drawing.Size(79, 29);
            this.ok.TabIndex = 6;
            this.ok.Text = "OK";
            this.ok.UseVisualStyleBackColor = true;
            this.ok.Click += new System.EventHandler(this.ok_Click);
            // 
            // cancel
            // 
            this.cancel.Location = new System.Drawing.Point(217, 141);
            this.cancel.Margin = new System.Windows.Forms.Padding(2);
            this.cancel.Name = "cancel";
            this.cancel.Size = new System.Drawing.Size(79, 29);
            this.cancel.TabIndex = 7;
            this.cancel.Text = "Cancel";
            this.cancel.UseVisualStyleBackColor = true;
            this.cancel.Click += new System.EventHandler(this.cancel_Click);
            // 
            // teamId
            // 
            this.teamId.Location = new System.Drawing.Point(76, 113);
            this.teamId.Margin = new System.Windows.Forms.Padding(2);
            this.teamId.Name = "teamId";
            this.teamId.Size = new System.Drawing.Size(219, 20);
            this.teamId.TabIndex = 5;
            // 
            // serviceTagLabel
            // 
            this.serviceTagLabel.AutoSize = true;
            this.serviceTagLabel.Location = new System.Drawing.Point(9, 115);
            this.serviceTagLabel.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.serviceTagLabel.Name = "serviceTagLabel";
            this.serviceTagLabel.Size = new System.Drawing.Size(48, 13);
            this.serviceTagLabel.TabIndex = 8;
            this.serviceTagLabel.Text = "Team ID";
            // 
            // team
            // 
            this.team.Location = new System.Drawing.Point(77, 90);
            this.team.Margin = new System.Windows.Forms.Padding(2);
            this.team.Name = "team";
            this.team.Size = new System.Drawing.Size(219, 20);
            this.team.TabIndex = 4;
            // 
            // teamLabel
            // 
            this.teamLabel.AutoSize = true;
            this.teamLabel.Location = new System.Drawing.Point(11, 93);
            this.teamLabel.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.teamLabel.Name = "teamLabel";
            this.teamLabel.Size = new System.Drawing.Size(34, 13);
            this.teamLabel.TabIndex = 10;
            this.teamLabel.Text = "Team";
            // 
            // UnRegisterForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(306, 181);
            this.Controls.Add(this.teamLabel);
            this.Controls.Add(this.team);
            this.Controls.Add(this.serviceTagLabel);
            this.Controls.Add(this.teamId);
            this.Controls.Add(this.cancel);
            this.Controls.Add(this.ok);
            this.Controls.Add(this.portLabel);
            this.Controls.Add(this.port);
            this.Controls.Add(this.address);
            this.Controls.Add(this.addressLabel);
            this.Controls.Add(this.descLabel);
            this.Margin = new System.Windows.Forms.Padding(2);
            this.Name = "UnRegisterForm";
            this.Text = "Soa #1 - UnRegister";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label descLabel;
        private System.Windows.Forms.Label addressLabel;
        private System.Windows.Forms.TextBox address;
        private System.Windows.Forms.TextBox port;
        private System.Windows.Forms.Label portLabel;
        private System.Windows.Forms.Button ok;
        private System.Windows.Forms.Button cancel;
        private System.Windows.Forms.TextBox teamId;
        private System.Windows.Forms.Label serviceTagLabel;
        private System.Windows.Forms.TextBox team;
        private System.Windows.Forms.Label teamLabel;
    }
}