using Purchase_Totaller.hl7;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.logic
{
    class PurchaseTotaller : IDisposable
    {
        private readonly Socket socket;
        private readonly string teamName;
        private readonly int port;
        public PurchaseTotaller(string teamName, int port)
        {
            this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            this.teamName = teamName;
            this.port = port;
        }

        public void Dispose()
        {
            socket.Dispose();
        }

        public void Listen()
        {
            this.socket.Bind(new IPEndPoint(IPAddress.Any, port));
            this.socket.Listen(30);
            this.socket.ReceiveTimeout = 30;
            this.socket.SendTimeout = 30;

            var registryConnection = new ServiceConnection(teamName);
            var connection = this.socket.Accept();
            try
            {
                var recv = new byte[2048];
                connection.Receive(recv);

                var received = Encoding.UTF8.GetString(recv);

                var request = ExecuteServiceServerRequest.FromMessage(received);

                bool allow = false;
                try
                {
                    var query = registryConnection.QueryTeam(request.Call.CallerTeamName,
                        request.Call.CallerTeamId, request.Call.ServiceName);
                    allow = true;
                }
                catch (FailureResponseException)
                {
                    // TODO: Log...
                }

                if (allow)
                {
                    //new ExecuteServiceServerResponse();
                    //Encoding.UTF8.GetBytes();
                    //socket.Send(
                }
                else
                {
                    // TODO: Return disallow
                }
            }
            catch (FailureRequestException)
            {
                throw;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (connection != null && connection.Connected)
                {
                    connection.Disconnect(false);
                    connection.Close();
                }
            }
        }

    }
}
