using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class HlConnection : IDisposable
    {
        public readonly string TeamName;

        private int? teamId = null;
        public int? TeamId
        {
            get 
            { 
                return teamId;
            }
        }

        private readonly Socket socket;
        private readonly IPAddress ip;
        private readonly int port;
        public HlConnection(string teamName): this(teamName, IPAddress.Parse("127.0.0.1"), 3128)
        {
        }
    
        public HlConnection(string teamName, IPAddress ip, int port)
        {
            this.TeamName = teamName;
            this.ip = ip;
            this.port = port;

            this.socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        }

        private void Connect()
        {
            socket.Connect(ip, port);
        }

        public void Dispose()
        {
            if (socket.Connected)
            {
                socket.Disconnect(false);
                socket.Close();
            }
        }

        private Response IssueRequest(Request request)
        {
            socket.Send(Encoding.ASCII.GetBytes(request.ToString()));

            var recv = new List<ArraySegment<byte>> ();
            socket.Receive(recv);

            // TODO: Create factory
            return null;
        }

        public RegisterTeamResponse Register()
        {
            var request = new RegisterTeamRequest(TeamName);
            throw new NotImplementedException();
        }

        public UnRegisterTeamResponse UnRegister()
        {
            throw new NotImplementedException();
        }

        public PublishServiceResponse Publish(HlService service)
        {
            throw new NotImplementedException();
        }

        public QueryTeamResponse QueryTeam(string p1, int teamId, string p2)
        {
            throw new NotImplementedException();
        }

        public QueryServiceResponse QueryService(string p)
        {
            throw new NotImplementedException();
        }

        public void ExecuteService(HlServiceCall call)
        {
            throw new NotImplementedException();
        }

        public bool IsRegistered()
        {
            // TODO: Double check with the server
            return String.IsNullOrWhiteSpace(TeamName) || teamId == null;
        }

    }
}
