using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Hl7Lib;
using System.Threading;
using Shared;

namespace SoaServer
{
    /// <summary>
    /// Service for the purchase totaller
    /// </summary>
    class PurchaseTotaller : IDisposable
    {
        /// <summary>
        /// Constant for argument name
        /// </summary>
        private const string ProvinceCode = "ProvinceCode";

        /// <summary>
        /// Constant for argument name
        /// </summary>
        private const string SubTotal = "SubTotal";

        /// <summary>
        /// Connection to the registry
        /// </summary>
        private ServiceConnection registryConnection;

        /// <summary>
        /// Socket for the listening port
        /// </summary>
        private readonly Socket socket;

        /// <summary>
        /// Name of the service
        /// </summary>
        private readonly string serviceName;

        /// <summary>
        /// Service tag
        /// </summary>
        private readonly string tagName;

        /// <summary>
        /// Team name
        /// </summary>
        private readonly string teamName;

        /// <summary>
        /// Registry ip
        /// </summary>
        private readonly IPAddress registryIp;

        /// <summary>
        /// Registry port
        /// </summary>
        private readonly int registryPort;

        /// <summary>
        /// Service ip
        /// </summary>
        private readonly IPAddress serviceIp;

        /// <summary>
        /// Service port
        /// </summary>
        private readonly int servicePort;

        /// <summary>
        /// Team id
        /// </summary>
        private int teamId;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="serviceName">Name of hosted service</param>
        /// <param name="tagName">Tag of hosted service</param>
        /// <param name="teamName">Team name of hosted service</param>
        /// <param name="registryIp">Registry ip</param>
        /// <param name="registryPort">Registry port</param>
        /// <param name="serviceIp">Service ip</param>
        /// <param name="servicePort">Service port</param>
        public PurchaseTotaller(string serviceName, string tagName, string teamName, IPAddress registryIp, int registryPort,
            IPAddress serviceIp, int servicePort)
        {
            this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            this.serviceName = serviceName;
            this.tagName = tagName;
            this.teamName = teamName;
            this.registryIp = registryIp;
            this.registryPort = registryPort;
            this.serviceIp = serviceIp;
            this.servicePort = servicePort;

            registryConnection = new ServiceConnection(Program.Logger, teamName, registryIp, registryPort);
        }

        /// <summary>
        /// Dispose the socket
        /// </summary>
        public void Dispose()
        {
            socket.Dispose();
        }

        /// <summary>
        /// Register the service with the registry
        /// </summary>
        public void RegisterService()
        {
            var registerResponse = registryConnection.Register();
            teamId = registerResponse.TeamId;

            try
            {
                var serviceDescriptor = new RemoteService(serviceIp, servicePort,
                    serviceName, tagName, 2, "Tax calculator for Canada");

                var provinceArg = new ServiceArgument(1, ProvinceCode, ServiceDataType.Tstring, true);
                serviceDescriptor.Args.Add(provinceArg);

                var subTotalArg = new ServiceArgument(2, SubTotal, ServiceDataType.Tdouble, true);
                serviceDescriptor.Args.Add(subTotalArg);

                serviceDescriptor.Returns.Add(PurchaseTotallerResponse.SubTotalReturn);
                serviceDescriptor.Returns.Add(PurchaseTotallerResponse.PstReturn);
                serviceDescriptor.Returns.Add(PurchaseTotallerResponse.HstReturn);
                serviceDescriptor.Returns.Add(PurchaseTotallerResponse.GstReturn);
                serviceDescriptor.Returns.Add(PurchaseTotallerResponse.TotalReturn);

                registryConnection.Publish(serviceDescriptor);
            }
            catch (FailureResponseException ex)
            {
                if (ex.ErrorCode == "-4" && ex.ErrorMessage.Contains("already published service"))
                {
                    // Ignore...
                }
                else
                {
                    throw;
                }
            }
        }

        /// <summary>
        /// Listen on the port for receiving incoming connections from clients
        /// </summary>
        public void Listen()
        {
            this.socket.Bind(new IPEndPoint(IPAddress.Any, servicePort));
            this.socket.Listen(30);
            this.socket.ReceiveTimeout = 30000;
            this.socket.SendTimeout = 30000;

            var alive = true;
            while (alive)
            {
                var connection = this.socket.Accept();

                // Start a new thread for the connection
                var thread = new Thread((o) =>
                {
                    var logger = Program.Logger;
                    try
                    {
                        var recv = new byte[2048];
                        connection.Receive(recv);

                        var received = Encoding.UTF8.GetString(recv);

                        var request = ExecuteServiceServerRequest.FromMessage(received);

                        logger.Write("Receiving service request : ");
                        foreach (var line in Response.CleanContents(received).Split(Request.NewRow.ToCharArray()))
	                    {
                            logger.Write("  >> {0}", line);
	                    }
                        logger.Write("---");

                        // Check if client has security privileges, will throw exception otherwise
                        var query = registryConnection.QueryTeam(request.Call.CallerTeamName,
                            request.Call.CallerTeamId, tagName);

                        var args = request.Call.Args;
                        var provinceCode = args.Where(a => a.Name == ProvinceCode).Select(a => a.Value).FirstOrDefault();
                        var subtotal = double.Parse(args.Where(a => a.Name == SubTotal).Select(a => a.Value).FirstOrDefault());

                        var serviceResponse = new PurchaseTotallerResponse(provinceCode, subtotal);
                        logger.Write("Responding to service request : ");
                        foreach (var line in Response.CleanContents(serviceResponse.ToString())
                            .Split(Request.NewRow.ToCharArray()))
                        {
                            logger.Write("  >> {0}", line);
                        }
                        logger.Write("---");

                        var responseBytes = Encoding.UTF8.GetBytes(serviceResponse.ToHl7());
                        connection.Send(responseBytes);
                    }
                    catch (FailureResponseException ex)
                    {

                        // Send the client a failure response
                        var response = new FailureResponse(ex.ErrorCode, ex.Message);
                        logger.Write("Responding to service request : ");
                        foreach (var line in Response.CleanContents(response.ToString())
                            .Split(Request.NewRow.ToCharArray()))
                        {
                            logger.Write("  >> {0}", line);
                        }
                        logger.Write("---");

                        var responseBytes = Encoding.UTF8.GetBytes(response.ToHl7());
                        connection.Send(responseBytes);
                    }
                    catch (Exception ex)
                    {
                        Program.Logger.Write(ex);

                        // Send the client a failure response
                        var response = new FailureResponse("-6", ex.Message);
                        logger.Write("Responding to service request : ");
                        foreach (var line in Response.CleanContents(response.ToString())
                            .Split(Request.NewRow.ToCharArray()))
                        {
                            logger.Write("  >> {0}", line);
                        }
                        logger.Write("---");

                        var responseBytes = Encoding.UTF8.GetBytes(response.ToHl7());
                        connection.Send(responseBytes);
                    }
                    finally
                    {
                        if (connection != null && connection.Connected)
                        {
                            connection.Disconnect(false);
                            connection.Close();
                        }
                    }
                });

                thread.Start(connection); 
            }
        }

    }
}
