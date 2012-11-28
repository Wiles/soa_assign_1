namespace Hl7Lib
{
    partial class ConnectForm
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
            this.serviceTag = new System.Windows.Forms.TextBox();
            this.serviceTagLabel = new System.Windows.Forms.Label();
            this.team = new System.Windows.Forms.TextBox();
            this.teamLabel = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // descLabel
            // 
            this.descLabel.AutoSize = true;
            this.descLabel.Location = new System.Drawing.Point(12, 9);
            this.descLabel.Name = "descLabel";
            this.descLabel.Size = new System.Drawing.Size(183, 17);
            this.descLabel.TabIndex = 0;
            this.descLabel.Text = "Connect to Service Registry";
            // 
            // addressLabel
            // 
            this.addressLabel.AutoSize = true;
            this.addressLabel.Location = new System.Drawing.Point(12, 55);
            this.addressLabel.Name = "addressLabel";
            this.addressLabel.Size = new System.Drawing.Size(60, 17);
            this.addressLabel.TabIndex = 1;
            this.addressLabel.Text = "Address";
            // 
            // address
            // 
            this.address.Location = new System.Drawing.Point(103, 55);
            this.address.Name = "address";
            this.address.Size = new System.Drawing.Size(291, 22);
            this.address.TabIndex = 2;
            // 
            // port
            // 
            this.port.Location = new System.Drawing.Point(103, 83);
            this.port.Name = "port";
            this.port.Size = new System.Drawing.Size(79, 22);
            this.port.TabIndex = 3;
            // 
            // portLabel
            // 
            this.portLabel.AutoSize = true;
            this.portLabel.Location = new System.Drawing.Point(12, 86);
            this.portLabel.Name = "portLabel";
            this.portLabel.Size = new System.Drawing.Size(34, 17);
            this.portLabel.TabIndex = 4;
            this.portLabel.Text = "Port";
            // 
            // ok
            // 
            this.ok.Location = new System.Drawing.Point(238, 174);
            this.ok.Name = "ok";
            this.ok.Size = new System.Drawing.Size(75, 23);
            this.ok.TabIndex = 5;
            this.ok.Text = "OK";
            this.ok.UseVisualStyleBackColor = true;
            this.ok.Click += new System.EventHandler(this.ok_Click);
            // 
            // cancel
            // 
            this.cancel.Location = new System.Drawing.Point(319, 174);
            this.cancel.Name = "cancel";
            this.cancel.Size = new System.Drawing.Size(75, 23);
            this.cancel.TabIndex = 6;
            this.cancel.Text = "Cancel";
            this.cancel.UseVisualStyleBackColor = true;
            this.cancel.Click += new System.EventHandler(this.cancel_Click);
            // 
            // serviceTag
            // 
            this.serviceTag.Location = new System.Drawing.Point(102, 139);
            this.serviceTag.Name = "serviceTag";
            this.serviceTag.Size = new System.Drawing.Size(291, 22);
            this.serviceTag.TabIndex = 7;
            // 
            // serviceTagLabel
            // 
            this.serviceTagLabel.AutoSize = true;
            this.serviceTagLabel.Location = new System.Drawing.Point(12, 142);
            this.serviceTagLabel.Name = "serviceTagLabel";
            this.serviceTagLabel.Size = new System.Drawing.Size(84, 17);
            this.serviceTagLabel.TabIndex = 8;
            this.serviceTagLabel.Text = "Service Tag";
            // 
            // team
            // 
            this.team.Location = new System.Drawing.Point(103, 111);
            this.team.Name = "team";
            this.team.Size = new System.Drawing.Size(291, 22);
            this.team.TabIndex = 9;
            // 
            // teamLabel
            // 
            this.teamLabel.AutoSize = true;
            this.teamLabel.Location = new System.Drawing.Point(15, 115);
            this.teamLabel.Name = "teamLabel";
            this.teamLabel.Size = new System.Drawing.Size(44, 17);
            this.teamLabel.TabIndex = 10;
            this.teamLabel.Text = "Team";
            // 
            // ConnectForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(413, 209);
            this.Controls.Add(this.teamLabel);
            this.Controls.Add(this.team);
            this.Controls.Add(this.serviceTagLabel);
            this.Controls.Add(this.serviceTag);
            this.Controls.Add(this.cancel);
            this.Controls.Add(this.ok);
            this.Controls.Add(this.portLabel);
            this.Controls.Add(this.port);
            this.Controls.Add(this.address);
            this.Controls.Add(this.addressLabel);
            this.Controls.Add(this.descLabel);
            this.Name = "ConnectForm";
            this.Text = "Soa #1 - Connect";
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
        private System.Windows.Forms.TextBox serviceTag;
        private System.Windows.Forms.Label serviceTagLabel;
        private System.Windows.Forms.TextBox team;
        private System.Windows.Forms.Label teamLabel;
    }
}