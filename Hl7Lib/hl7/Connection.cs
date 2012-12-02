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
    /// <summary>
    /// Connection to a valid Soa1 registry or service
    /// </summary>
    public class ServiceConnection
    {
        /// <summary>
        /// Name of connecting team
        /// </summary>
        public readonly string TeamName;

        /// <summary>
        /// ID of connected team
        /// </summary>
        private int? teamId = null;

        /// <summary>
        /// ID of connected team
        /// </summary>
        public int? TeamId
        {
            get
            {
                return teamId;
            }
        }

        /// <summary>
        /// Logger for logging events
        /// </summary>
        private readonly Logger logger;
        /// <summary>
        /// IP address of registry or service
        /// </summary>
        private readonly IPAddress ip;
        /// <summary>
        /// Port of registry or service
        /// </summary>
        private readonly int port;
        /// <summary>
        /// Enforce that the client must be registered, before
        /// any requests can be made
        /// </summary>
        private readonly bool enforceRegister;

        /// <summary>
        /// </summary>
        /// <param name="logger">Logger</param>
        /// <param name="teamName">Name of client team</param>
        public ServiceConnection(Logger logger, string teamName)
            : this(logger, teamName, IPAddress.Parse("127.0.0.1"), 3128)
        {
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="logger">Logger for events</param>
        /// <param name="teamName">Name of client team</param>
        /// <param name="ip">IP address of registry or service</param>
        /// <param name="port">Port of registry or service</param>
        /// <param name="enforceRegister">
        /// Enforce that the client must be registered, before
        /// any requests can be made
        /// </param>
        /// <param name="teamId">ID of connected team</param>
        public ServiceConnection(Logger logger, string teamName, IPAddress ip, 
            int port, bool enforceRegister = true, int? teamId = null)
        {
            this.TeamName = teamName;
            this.ip = ip;
            this.port = port;
            this.enforceRegister = enforceRegister;
            this.teamId = teamId;
            this.logger = logger;
        }

        /// <summary>
        /// Issue a request to the registry or service
        /// </summary>
        /// <param name="request">Request to issue</param>
        /// <returns>Response received</returns>
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

        /// <summary>
        /// Register with the registry (may not register with a service)
        /// </summary>
        /// <returns>Response</returns>
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

        /// <summary>
        /// UnRegister with the registry (may not unregister with a service)
        /// </summary>
        /// <returns>Response</returns>
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

        /// <summary>
        /// Publish a service to the registry
        /// </summary>
        /// <param name="service">Service to publish</param>
        /// <returns>Response</returns>
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

        /// <summary>
        /// Query a team to see if they have privileges to execute a certain service tag
        /// </summary>
        /// <param name="queryTeamName">Name of team</param>
        /// <param name="queryTeamId">ID of team</param>
        /// <param name="serviceTag">Service tag</param>
        /// <returns>Response</returns>
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

        /// <summary>
        /// Query a service by service tag
        /// </summary>
        /// <param name="serviceTag">Service tag</param>
        /// <returns>Response</returns>
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

        /// <summary>
        /// Execute service
        /// </summary>
        /// <param name="call">Execution information</param>
        /// <returns>Response</returns>
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

        /// <summary>
        /// Is the connection registered?
        /// WARNING: FOR NOW THIS IS A LOCAL CHECK AND DOES NOT ACTUALLY
        /// CHECK WITH THE REGISTRY
        /// </summary>
        /// <returns></returns>
        public bool IsRegistered()
        {
            // TODO: Double check with the server
            var registered = !String.IsNullOrWhiteSpace(TeamName) && teamId != null;
            return registered;
        }

        /// <summary>
        /// Enforce that the client is locally registered
        /// </summary>
        private void EnforceRegistered()
        {
            if (enforceRegister && !IsRegistered())
            {
                throw new NotRegisteredException();
            }
        }

        /// <summary>
        /// Is this response a failure?
        /// </summary>
        /// <param name="response">Response to check</param>
        /// <returns>Whether the response is a failure response</returns>
        private bool IsFailureResponse(Response response)
        {
            return response is FailureResponse;
        }

        /// <summary>
        /// Convert a response to a failure exception if the response is a failure response.
        /// 
        /// Otherwise, invalid response type exception
        /// </summary>
        /// <param name="response">Response</param>
        /// <returns>Exception</returns>
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

    /// <summary>
    /// Client is not registered exception
    /// </summary>
    public class NotRegisteredException : Exception
    {
        /// <summary>
        /// </summary>
        public NotRegisteredException()
        {
        }

        /// <summary>
        /// </summary>
        /// <param name="message">Error message</param>
        /// <param name="e">Base exception</param>
        public NotRegisteredException(string message, Exception e)
            : base(message, e)
        {
        }
    }
}
