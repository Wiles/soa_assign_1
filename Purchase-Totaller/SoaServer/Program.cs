using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Hl7Lib;
using Shared;

namespace SoaServer
{
    class Program
    {
        public static string ConfigName = "server";

        public static Logger Logger
        {
            get
            {
                return Logger.GetInstance(ConfigName);
            }
        }

        static void Main(string[] args)
        {
            var teamName = "";
            var tagName = "";
            var serviceName = "";
            var port = 0;
            try
            {
                teamName = ConfigurationSettings.AppSettings["teamName"];
            }
            catch (Exception ex)
            {
                Console.WriteLine("Please enter a teamName in the App.Config");
                return;
            }

            try
            {
                tagName = ConfigurationSettings.AppSettings["tagName"];
            }
            catch (Exception ex)
            {
                Console.WriteLine("Please enter a tagName in the App.Config");
                return;
            }

            try
            {
                serviceName = ConfigurationSettings.AppSettings["serviceName"];
            }
            catch (Exception ex)
            {
                Console.WriteLine("Please enter a serviceName in the App.Config");
                return;
            }

            try
            {
                port = int.Parse(ConfigurationSettings.AppSettings["port"]);
            }
            catch (Exception)
            {
                Console.WriteLine("Please enter a port (valid number) in the App.Config");
                return;
            }

            Logger.Write("=======================================================");
            Logger.Write("Team    : {0}", teamName);
            Logger.Write("Tag-Name: {0}", tagName);
            Logger.Write("Service : {0}", serviceName);
            Logger.Write("=======================================================");
            Logger.Write("---");



            var totaller = new PurchaseTotaller(teamName, port);
            totaller.RegisterService();
            totaller.Listen();
        }
        
    }
}
