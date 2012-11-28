using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Hl7Lib
{
    public class Response
    {
    }

    public class RegisterTeamResponse : Response
    {
        public int TeamId;
        public string Expiration;
    }

    public class UnRegisterTeamResponse : Response
    {
    }

    public class QueryTeamResponse : Response
    {
    }

    public class PublishServiceResponse : Response
    {
    }

    public class QueryServiceResponse : Response
    {
        public readonly RemoteService Service;
        public QueryServiceResponse(RemoteService service)
        {
            this.Service = service;
        }
    }

    public class ExecuteServiceResponse : Response
    {
        private RemoteServiceReturn Returned;

        public ExecuteServiceResponse(RemoteServiceReturn returned)
        {
            this.Returned = returned;
        }
    }

    public class FailureResponse : Response
    {
        public readonly string ErrorCode;
        public readonly string ErrorMessage;
        public readonly FailureResponseException Exception;
        public FailureResponse(string errorCode, string errorMessage)
        {
            this.ErrorCode = errorCode;
            this.ErrorMessage = errorMessage;
            this.Exception = new FailureResponseException(errorCode, errorMessage);
        }
    }

    public class FailureResponseException : Exception
    {
        public FailureResponseException(FailureResponse response) 
            : base(String.Format("Error: {0}, because {1}", response.ErrorCode, response.ErrorMessage))
        {

        }

        public FailureResponseException(string errorCode, string errorMessage, Exception e = null)
            : base(String.Format("Error: {0}, because {1}", errorCode, errorMessage), e)
        {
        }
    }


    public class InvalidResponseTypeException : Exception
    {
        public InvalidResponseTypeException()
        {
        }

        public InvalidResponseTypeException(string message, Exception e): base(message, e)
        {
        }
    }

    public class ResponseFactory
    {
        private bool IsOkMessage(string message)
        {
            try
            {
                return message.Split(Request.Delimiter.ToCharArray())[1] == "OK";
            }
            catch (Exception ex)
            {
                throw new Exception("Failure to create if message is ok/failure: " + ex.Message);
            }
        }

        private FailureResponse CreateFailureException(string message)
        {
            try
            {
                var split = message.Split(Request.Delimiter.ToCharArray());

                var isFailureMessage = (split[1] == "NOT-OK");
                if (!isFailureMessage)
                {
                    throw new Exception("Improper header on message");
                }

                var errorCode = split[2];
                var errorMessage = split[3];

                return new FailureResponse(errorCode, errorMessage);
            }
            catch (Exception ex)
            {
                throw new Exception("Failure to create failure exception because: " + ex.Message);
            }
        }

        public List<string[]> ExtractRows(string message)
        {
            var rows = new List<string[]>();
            var lines = message.Split(Request.NewRow.ToCharArray());
            foreach (var line in lines)
            {
                rows.Add(line.Split(Request.Delimiter.ToCharArray()));
            }

            return rows;
        }

        public Response FromMessage(Request request, string message)
        {
            if (!IsOkMessage(message))
            {
                return CreateFailureException(message);
            }

            var rows = ExtractRows(message);
            if (request is RegisterTeamRequest)
            {
                var fullRequest = (RegisterTeamRequest)request;
                var response = new RegisterTeamResponse();

                var teamId = rows[0][2];
                var expiration = rows[0][3];

                try
                {
                    response.TeamId = int.Parse(teamId);
                    response.Expiration = expiration;

                    return response;
                }
                catch (Exception)
                {
                    throw new FormatException("Failure to parse teamId, must be integer");
                }
            }
            else if (request is UnRegisterTeamRequest)
            {
                var fullRequest = (UnRegisterTeamRequest)request;
                var response = new UnRegisterTeamResponse();
                return response;
            }
            else if (request is QueryTeamRequest)
            {
                var fullRequest = (QueryTeamRequest)request;
                var response = new QueryTeamResponse();
                // TODO: Query Team Response
                return response;
            }
            else if (request is PublishServiceRequest)
            {
                var fullRequest = (PublishServiceRequest)request;
                var response = new PublishServiceResponse();
                return response;
            }
            else if (request is QueryServiceRequest)
            {
                var fullRequest = (QueryServiceRequest)request;

                var numSegments = int.Parse(rows[0][4]);
                var serviceName = rows[1][2];
                var numArgs = int.Parse(rows[1][4]);
                var numResponses = int.Parse(rows[1][5]);

                var mchIndex = 2 + numArgs + numResponses;
                var ip = rows[mchIndex][1];
                var port = int.Parse(rows[mchIndex][2]);
                var service = new RemoteService(IPAddress.Parse(ip), port, serviceName, fullRequest.TagName);

                var response = new QueryServiceResponse(service);
                // Read the args
                for (int i = 0; i < numArgs; i++)
                {
                    var arg = 2 + i;
                    var row = rows[arg];

                    var pos = int.Parse(row[1]);
                    var argName = row[2];
                    var dataType = ServiceArgument.TypeFromString(row[3]);
                    var mandatory = false;
                    
                    try 
	                {
                        // Optional
		                mandatory = row[4].Equals("mandatory", StringComparison.CurrentCultureIgnoreCase);
	                }
	                catch (Exception)
	                {
		                // Ignore...
	                }

                    service.Args.Add(new ServiceArgument(pos, argName, dataType, mandatory));
                }

                // Read the responses
                for (int i = 0; i < numResponses; i++)
			    {
                    var resp = 2 + i + numArgs;
			        var row = rows[resp];
                    
                    var pos = int.Parse(row[1]);
                    var respName = row[2];
                    var dataType = ServiceArgument.TypeFromString(row[3]);

                    service.Returns.Add(new ServiceReturn(pos, respName, dataType));
			    }

                return response;
            }
            else if (request is ExecuteServiceRequest)
            {
                var fullRequest = (ExecuteServiceRequest)request;

                var numSegments = int.Parse(rows[0][4]);
                var name = rows[1][2];
                var numArgs = int.Parse(rows[1][4]);
                var numResponses = int.Parse(rows[1][5]);
                    
                var returnedCall = new RemoteServiceReturn(name);

                for (int arg = 0; arg < numArgs; arg++)
                {
                    var row = rows[1 + arg];
                    Debug.Assert(row[0] == "ARG");

                    var pos = int.Parse(row[1]);
                    var argName = row[2];
                    var dataType = row[3];
                    bool? mandatory = null;

                    try 
	                {
                        // This is optional
		                mandatory = row[4].Equals("mandatory", StringComparison.CurrentCultureIgnoreCase);
	                }
	                catch (Exception)
	                {
                        // Ignore..
	                }

                    var serviceArgument = new ServiceArgument(pos, argName, dataType, 
                        ((mandatory == null) ? false : (bool)mandatory));
                    returnedCall.Args.Add(serviceArgument);
                }


                for (int resp = 0; resp < numResponses; resp++)
                {
                    var row = rows[1 + numArgs + resp];
                    Debug.Assert(row[0] == "RSP");

                    var pos = int.Parse(row[1]);
                    var argName = row[2];
                    var dataType = row[3];

                    var serviceReturn = new ServiceReturn(pos, argName, ServiceArgument.TypeFromString(dataType));
                    returnedCall.Returns.Add(serviceReturn);
                }

                var response = new ExecuteServiceResponse(returnedCall);
                return response;
            }
            else
            {
                throw new KeyNotFoundException("Failure to create response for request of type: " + request.GetType().Name);
            }

        }
    }
}
