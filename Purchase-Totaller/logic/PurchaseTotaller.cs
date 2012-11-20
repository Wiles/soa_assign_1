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
        private readonly int port;
        public PurchaseTotaller(int port)
        {
            this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            this.port = port;
        }

        public void Dispose()
        {
        }

        public void Listen()
        {
            this.socket.Bind(new IPEndPoint(IPAddress.Any, port));
            this.socket.Listen(30);

            var connection = this.socket.Accept();
            var recv = new byte[2048];
            connection.Receive(recv);

            var received = Encoding.UTF8.GetString(recv);

            ExecuteServiceRequest.FromMessage(received);
            // TODO: Request from message


            // TODO: Authenticate

        }

    }
}
