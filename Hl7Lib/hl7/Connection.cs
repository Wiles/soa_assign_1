using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Shared;

namespace Hl7Lib
{
    public class ServiceConnection
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

        private readonly Logger logger;
        private readonly IPAddress ip;
        private readonly int port;
        private readonly bool enforceRegister;
        public ServiceConnection(Logger logger, string teamName)
            : this(logger, teamName, IPAddress.Parse("127.0.0.1"), 3128)
        {
        }

        public ServiceConnection(Logger logger, string teamName, IPAddress ip, int port, bool enforceRegister = true, int? teamId = null)
        {
            this.TeamName = teamName;
            this.ip = ip;
            this.port = port;
            this.enforceRegister = enforceRegister;
            this.teamId = teamId;
            this.logger = logger;
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

                if (request is ExecuteServiceRequest)
                {
                    logger.Write("Sending service request to IP {0}, PORT {1} :", ip.ToString(), port.ToString());
                }
                else
                {
                    logger.Write("Calling SOA-Registry with message :");
                }

                foreach (var line in Response.CleanContents(request.ToString()).Split(Request.NewRow.ToCharArray()))
                {
                    logger.Write("  >> {0}", line);
                }
                logger.Write("---");

                socket.ReceiveTimeout = 30000;
                socket.SendTimeout = 30000;
                socket.Send(Encoding.ASCII.GetBytes(request.ToString()));

                // Read until the end marker is reached
                var sb = new StringBuilder();
                do
                {
                    byte[] recv = new byte[2048];
                    socket.Receive(recv);
                    var receivedMessage = Encoding.UTF8.GetString(recv);
                    sb.Append(receivedMessage);

                } while (!sb.ToString().Replace("\0", "").Contains((char)28));


                if (request is ExecuteServiceRequest)
                {
                    logger.Write("  >> Response from Published Service :");
                }
                else
                {
                    logger.Write("  >> Response from SOA-Registry :");
                }

                foreach (var line in Response.CleanContents(sb.ToString()).Split(Request.NewRow.ToCharArray()))
                {
                    logger.Write("     >> {0}", line);
                }
                logger.Write("---");


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

        public PublishServiceResponse Publish(RemoteService service)
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
            if (enforceRegister && !IsRegistered())
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

        public NotRegisteredException(string message, Exception e)
            : base(message, e)
        {
        }
    }
}
