using Shared;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SoaClient
{
    static class Program
    {
        /// <summary>
        /// Name of logger
        /// </summary>
        public static string ConfigName = "client";

        /// <summary>
        /// Logger for the client application
        /// </summary>
        public static Logger Logger
        {
            get
            {
                return Shared.Logger.GetInstance(ConfigName);
            }
        }

        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainForm());
        }
    }
}
