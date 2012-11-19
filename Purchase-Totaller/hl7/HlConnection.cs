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
            if (!socket.Connected)
            {
                Connect();
            }

            if (!IsRegistered() && !(request is RegisterTeamRequest))
            {
                Register();
            }

            socket.Send(Encoding.ASCII.GetBytes(request.ToString()));

            // Read until the end marker is reached
            var sb = new StringBuilder();
            do
            {
                byte[] recv = new byte[2048];
                int count = socket.Receive(recv, recv.Length, SocketFlags.None);
                var receivedMessage = Encoding.ASCII.GetString(recv);
                sb.Append(receivedMessage);

            } while (sb.ToString().Last() != Request.EndMarker.ToCharArray()[0]);

            var fact = new ResponseFactory();
            var response = fact.FromMessage(request, sb.ToString());

            return response;
        }

        public RegisterTeamResponse Register()
        {
            var request = new RegisterTeamRequest(TeamName);
            
            var response = IssueRequest(request);
            if (response is RegisterTeamResponse)
            {
                var full = (RegisterTeamResponse)response;
                teamId = full.TeamId;
                return full;
            }
            else
            {
                throw new InvalidResponseTypeException();
            }
        }

        public UnRegisterTeamResponse UnRegister()
        {
            if (IsRegistered())
            {
                var response = IssueRequest(new UnRegisterTeamRequest(TeamName, (int)TeamId));
                if (response is UnRegisterTeamResponse)
                {
                    teamId = null;
                    return (UnRegisterTeamResponse)response;
                }
                else
                {
                    throw new InvalidResponseTypeException();
                }
            }
            else
            {
                throw new NotRegisteredException();
            }
        }

        public PublishServiceResponse Publish(LocalService service)
        {
            if (IsRegistered())
            {
                var response = IssueRequest(new PublishServiceRequest(TeamName, (int)TeamId, service));
                if (response is PublishServiceResponse)
                {
                    return (PublishServiceResponse)response;
                }
                else
                {
                    throw new InvalidResponseTypeException();
                }
            }
            else
            {
                throw new NotRegisteredException();
            }
        }

        public QueryTeamResponse QueryTeam(string queryTeamName, int queryTeamId, string serviceTag)
        {
            throw new NotImplementedException();
        }

        public QueryServiceResponse QueryService(string serviceTag)
        {
            throw new NotImplementedException();
        }

        public void ExecuteService(RemoteServiceCall call)
        {
            throw new NotImplementedException();
        }

        public bool IsRegistered()
        {
            // TODO: Double check with the server
            var registered = !String.IsNullOrWhiteSpace(TeamName) && teamId != null;
            return registered;
        }

    }

    public class NotRegisteredException : Exception
    {
        public NotRegisteredException()
        {
        }

        public NotRegisteredException(string message, Exception e) : base(message, e)
        {
        }
    }
}
