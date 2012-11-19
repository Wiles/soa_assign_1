using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class Response
    {
    }

    public class RegisterTeamResponse : Response
    {
        public int TeamId;
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
    }

    public class ExecuteServiceResponse : Response
    {
    }

    public class FailureResponse : Response
    {
        public readonly FailureResponseException Exception;
        public FailureResponse(string errorCode, string errorMessage)
        {
            this.Exception = new FailureResponseException(errorCode, errorMessage);
        }

        public FailureResponse(FailureResponseException exception)
        {
            this.Exception = exception;
        }
    }

    public class FailureResponseException : Exception
    {
        public FailureResponseException(string errorCode, string errorMessage, Exception e = null)
            : base
                (String.Format("Error: {0}, because {1}", errorCode, errorMessage), e)
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
                    // TODO: Expiration

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
                return response;
            }
            else if (request is PublishServiceRequest)
            {
                var fullRequest = (QueryTeamRequest)request;
                var response = new QueryTeamResponse();
                return response;
            }
            else if (request is QueryServiceRequest)
            {
                var fullRequest = (QueryServiceRequest)request;
                var response = new QueryServiceResponse();

                try
                {
                    var numSegments = int.Parse(rows[0][4]);


                }
                catch (Exception)
                {
                    
                    throw;
                }

                return response;
            }
            else if (request is ExecuteServiceRequest)
            {
                var fullRequest = (ExecuteServiceRequest)request;
                var response = new ExecuteServiceResponse();

                try
                {
                    var numSegments = int.Parse(rows[0][4]);
                    var name = rows[1][2];
                    var numArgs = int.Parse(rows[1][4]);
                    var numResponses = int.Parse(rows[1][5]);
                    
                    var call = new RemoteServiceCall(name);

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
		                    mandatory = bool.Parse(row[4]);
	                    }
	                    catch (Exception)
	                    {
                            // Ignore..
	                    }

                        var serviceArgument = new ServiceArgument(pos, argName, dataType, 
                            ((mandatory == null) ? false : (bool)mandatory));
                        call.Args.Add(serviceArgument);
                    }


                    for (int resp = 0; resp < numResponses; resp++)
                    {
                        var row = rows[1 + numArgs + resp];
                        Debug.Assert(row[0] == "RSP");

                        var pos = int.Parse(row[1]);
                        var argName = row[2];
                        var dataType = row[3];


                    }
                }
                catch (Exception)
                {

                    throw;
                }

                return response;
            }
            else
            {
                throw new KeyNotFoundException("Failure to create response for request of type: " + request.GetType().Name);
            }

        }
    }
}
