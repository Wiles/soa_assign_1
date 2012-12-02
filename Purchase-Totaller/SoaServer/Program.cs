using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Hl7Lib;
using Shared;
using System.Net;
using System.Threading;

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
            try
            {
                var teamName = "";
                var tagName = "";
                var serviceName = "";
                var registryIp = IPAddress.Any;
                var registryPort = 0;
                var ip = IPAddress.Any;
                var port = 0;

                var appSettings = ConfigurationSettings.AppSettings;
                try
                {
                    teamName = appSettings["teamName"];
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a teamName in the App.Config");
                    return;
                }

                try
                {
                    tagName = appSettings["tagName"];
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a tagName in the App.Config");
                    return;
                }

                try
                {
                    serviceName = appSettings["serviceName"];
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a serviceName in the App.Config");
                    return;
                }


                try
                {
                    registryIp = IPAddress.Parse(appSettings["registryIp"]);
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a valid serviceIp (valid ip address) in the App.Config");
                    return;
                }

                try
                {
                    registryPort = int.Parse(appSettings["registryPort"]);
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a registryPort (valid number) in the App.Config");
                    return;
                }

                try
                {
                    ip = IPAddress.Parse(appSettings["ip"]);
                }
                catch (Exception)
                {
                    Console.WriteLine("Please enter a valid ip (valid ip address) in the App.Config");
                    return;
                }

                try
                {
                    port = int.Parse(appSettings["port"]);
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

                var totaller = new PurchaseTotaller(serviceName, tagName, teamName, registryIp, registryPort, ip, port);
                totaller.RegisterService();

                RunRegisterThread(totaller, 30000);

                totaller.Listen();
            }
            catch (Exception ex)
            {
                Logger.Write(ex);
            }
        }

        private static void RunRegisterThread(PurchaseTotaller totaller, int checkIntervalMilliseconds)
        {
            try
            {
                var thread = new Thread((o) =>
                {
                    var ptotaller = (PurchaseTotaller)o;

                    ptotaller.RegisterService();

                    Thread.Sleep(checkIntervalMilliseconds);
                });

                thread.Start(totaller);
            }
            catch (Exception ex)
            {
                Logger.Write(ex);
            }
        }

    }
}
