using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class HlConnection
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
        }

        private Response IssueRequest(Request request)
        {
            var socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);

            try
            {
                socket.Connect(ip, port);

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
                    socket.Receive(recv);
                    var receivedMessage = Encoding.UTF8.GetString(recv);
                    sb.Append(receivedMessage);

                } while (!sb.ToString().Replace("\0", "").EndsWith(Request.EndOfMessage));

                var fact = new ResponseFactory();
                var response = fact.FromMessage(request, sb.ToString());

                return response;
            }
            catch (Exception)
            {
                throw;
            }
            finally
            {
                if (socket != null && socket.Connected)
                {
                    socket.Disconnect(false);
                    socket.Close();
                }
            }
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
                throw ThrowFailureResponse(response);
            }
        }

        public UnRegisterTeamResponse UnRegister()
        {
            EnforceRegistered();
            
            var response = IssueRequest(new UnRegisterTeamRequest(TeamName, (int)TeamId));
            if (response is UnRegisterTeamResponse)
            {
                teamId = null;
                return (UnRegisterTeamResponse)response;
            }
            else
            {
                throw ThrowFailureResponse(response);
            }
        }

        public PublishServiceResponse Publish(LocalService service)
        {
            EnforceRegistered();

            var response = IssueRequest(new PublishServiceRequest(TeamName, (int)TeamId, service));
            if (response is PublishServiceResponse)
            {
                return (PublishServiceResponse)response;
            }
            else
            {
                throw ThrowFailureResponse(response);
            }
        }

        public QueryTeamResponse QueryTeam(string queryTeamName, int queryTeamId, string serviceTag)
        {
            EnforceRegistered();

            var response = IssueRequest(new QueryTeamRequest(TeamName, 
                (int)TeamId, queryTeamName, queryTeamId, serviceTag));
            if (response is QueryTeamResponse)
            {
                return (QueryTeamResponse)response;
            }
            else
            {
                throw ThrowFailureResponse(response);
            }
        }

        public QueryServiceResponse QueryService(string serviceTag)
        {
            EnforceRegistered();

            var response = IssueRequest(new QueryServiceRequest(TeamName,
                (int)TeamId, serviceTag));
            if (response is QueryServiceResponse)
            {
                return (QueryServiceResponse)response;
            }
            else
            {
                throw ThrowFailureResponse(response);
            }
        }

        public ExecuteServiceResponse ExecuteService(RemoteServiceCall call)
        {
            EnforceRegistered();

            var response = IssueRequest(new ExecuteServiceRequest(TeamName,
                (int)TeamId, call));
            if (response is ExecuteServiceResponse)
            {
                return (ExecuteServiceResponse)response;
            }
            else
            {
                throw ThrowFailureResponse(response);
            }
        }

        public bool IsRegistered()
        {
            // TODO: Double check with the server
            var registered = !String.IsNullOrWhiteSpace(TeamName) && teamId != null;
            return registered;
        }

        private void EnforceRegistered()
        {
            if (!IsRegistered())
            {
                throw new NotRegisteredException();
            }
        }

        private bool IsFailureResponse(Response response)
        {
            return response is FailureResponse;
        }

        public Exception ThrowFailureResponse(Response response)
        {
            if (IsFailureResponse(response))
            {
                return new FailureResponseException((FailureResponse)response);
            }
            else
            {
                return new InvalidResponseTypeException();
            }
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
