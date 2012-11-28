namespace SoaClient
{
    partial class MainForm
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
            this.menuStrip = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.connectToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.exitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.runToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.runToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.splitContainer = new System.Windows.Forms.SplitContainer();
            this.argGrid = new System.Windows.Forms.DataGridView();
            this.respGrid = new System.Windows.Forms.DataGridView();
            this.RespName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.RespType = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.RespValue = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ArgName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ArgType = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ArgMandatory = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.ArgValue = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.menuStrip.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer)).BeginInit();
            this.splitContainer.Panel1.SuspendLayout();
            this.splitContainer.Panel2.SuspendLayout();
            this.splitContainer.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.argGrid)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.respGrid)).BeginInit();
            this.SuspendLayout();
            // 
            // menuStrip
            // 
            this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.runToolStripMenuItem});
            this.menuStrip.Location = new System.Drawing.Point(0, 0);
            this.menuStrip.Name = "menuStrip";
            this.menuStrip.Size = new System.Drawing.Size(930, 28);
            this.menuStrip.TabIndex = 0;
            this.menuStrip.Text = "menuStrip1";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.connectToolStripMenuItem,
            this.exitToolStripMenuItem});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(44, 24);
            this.fileToolStripMenuItem.Text = "&File";
            // 
            // connectToolStripMenuItem
            // 
            this.connectToolStripMenuItem.Name = "connectToolStripMenuItem";
            this.connectToolStripMenuItem.ShortcutKeys = System.Windows.Forms.Keys.F2;
            this.connectToolStripMenuItem.Size = new System.Drawing.Size(156, 24);
            this.connectToolStripMenuItem.Text = "&Connect";
            this.connectToolStripMenuItem.Click += new System.EventHandler(this.connectToolStripMenuItem_Click);
            // 
            // exitToolStripMenuItem
            // 
            this.exitToolStripMenuItem.Name = "exitToolStripMenuItem";
            this.exitToolStripMenuItem.Size = new System.Drawing.Size(156, 24);
            this.exitToolStripMenuItem.Text = "E&xit";
            this.exitToolStripMenuItem.Click += new System.EventHandler(this.exitToolStripMenuItem_Click);
            // 
            // runToolStripMenuItem
            // 
            this.runToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.runToolStripMenuItem1});
            this.runToolStripMenuItem.Name = "runToolStripMenuItem";
            this.runToolStripMenuItem.Size = new System.Drawing.Size(46, 24);
            this.runToolStripMenuItem.Text = "Run";
            // 
            // runToolStripMenuItem1
            // 
            this.runToolStripMenuItem1.Name = "runToolStripMenuItem1";
            this.runToolStripMenuItem1.ShortcutKeys = System.Windows.Forms.Keys.F5;
            this.runToolStripMenuItem1.Size = new System.Drawing.Size(152, 24);
            this.runToolStripMenuItem1.Text = "Run";
            this.runToolStripMenuItem1.Click += new System.EventHandler(this.runToolStripMenuItem1_Click);
            // 
            // splitContainer
            // 
            this.splitContainer.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer.Location = new System.Drawing.Point(0, 28);
            this.splitContainer.Name = "splitContainer";
            this.splitContainer.Orientation = System.Windows.Forms.Orientation.Horizontal;
            // 
            // splitContainer.Panel1
            // 
            this.splitContainer.Panel1.Controls.Add(this.argGrid);
            // 
            // splitContainer.Panel2
            // 
            this.splitContainer.Panel2.Controls.Add(this.respGrid);
            this.splitContainer.Size = new System.Drawing.Size(930, 676);
            this.splitContainer.SplitterDistance = 326;
            this.splitContainer.TabIndex = 1;
            // 
            // argGrid
            // 
            this.argGrid.AllowUserToAddRows = false;
            this.argGrid.AllowUserToDeleteRows = false;
            this.argGrid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.argGrid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.ArgName,
            this.ArgType,
            this.ArgMandatory,
            this.ArgValue});
            this.argGrid.Dock = System.Windows.Forms.DockStyle.Fill;
            this.argGrid.Location = new System.Drawing.Point(0, 0);
            this.argGrid.Name = "argGrid";
            this.argGrid.RowTemplate.Height = 24;
            this.argGrid.Size = new System.Drawing.Size(930, 326);
            this.argGrid.TabIndex = 0;
            // 
            // respGrid
            // 
            this.respGrid.AllowUserToAddRows = false;
            this.respGrid.AllowUserToDeleteRows = false;
            this.respGrid.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.respGrid.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.RespName,
            this.RespType,
            this.RespValue});
            this.respGrid.Dock = System.Windows.Forms.DockStyle.Fill;
            this.respGrid.Location = new System.Drawing.Point(0, 0);
            this.respGrid.Name = "respGrid";
            this.respGrid.ReadOnly = true;
            this.respGrid.RowTemplate.Height = 24;
            this.respGrid.Size = new System.Drawing.Size(930, 346);
            this.respGrid.TabIndex = 0;
            // 
            // RespName
            // 
            this.RespName.HeaderText = "Name";
            this.RespName.Name = "RespName";
            this.RespName.ReadOnly = true;
            // 
            // RespType
            // 
            this.RespType.HeaderText = "Type";
            this.RespType.Name = "RespType";
            this.RespType.ReadOnly = true;
            // 
            // RespValue
            // 
            this.RespValue.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.RespValue.HeaderText = "Value";
            this.RespValue.Name = "RespValue";
            this.RespValue.ReadOnly = true;
            // 
            // ArgName
            // 
            this.ArgName.HeaderText = "Name";
            this.ArgName.Name = "ArgName";
            // 
            // ArgType
            // 
            this.ArgType.HeaderText = "Type";
            this.ArgType.Name = "ArgType";
            // 
            // ArgMandatory
            // 
            this.ArgMandatory.HeaderText = "Mandatory";
            this.ArgMandatory.Name = "ArgMandatory";
            // 
            // ArgValue
            // 
            this.ArgValue.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.ArgValue.HeaderText = "Value";
            this.ArgValue.Name = "ArgValue";
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 16F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(930, 704);
            this.Controls.Add(this.splitContainer);
            this.Controls.Add(this.menuStrip);
            this.MainMenuStrip = this.menuStrip;
            this.Name = "MainForm";
            this.Text = "Soa #1 - Client";
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.menuStrip.ResumeLayout(false);
            this.menuStrip.PerformLayout();
            this.splitContainer.Panel1.ResumeLayout(false);
            this.splitContainer.Panel2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.splitContainer)).EndInit();
            this.splitContainer.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.argGrid)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.respGrid)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.MenuStrip menuStrip;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem connectToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem exitToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem runToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem runToolStripMenuItem1;
        private System.Windows.Forms.SplitContainer splitContainer;
        private System.Windows.Forms.DataGridView argGrid;
        private System.Windows.Forms.DataGridView respGrid;
        private System.Windows.Forms.DataGridViewTextBoxColumn RespName;
        private System.Windows.Forms.DataGridViewTextBoxColumn RespType;
        private System.Windows.Forms.DataGridViewTextBoxColumn RespValue;
        private System.Windows.Forms.DataGridViewTextBoxColumn ArgName;
        private System.Windows.Forms.DataGridViewTextBoxColumn ArgType;
        private System.Windows.Forms.DataGridViewTextBoxColumn ArgMandatory;
        private System.Windows.Forms.DataGridViewTextBoxColumn ArgValue;
    }
}

