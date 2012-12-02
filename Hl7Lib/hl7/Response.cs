using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Hl7Lib
{
    /// <summary>
    /// Base class for all responses
    /// </summary>
    public class Response
    {
        /// <summary>
        /// Clean the HL7 of a request or response
        /// 
        /// Remove '\0', Beginning Marker and Ending marker
        /// </summary>
        /// <param name="responseContents">HL7</param>
        /// <returns>Cleaned contents</returns>
        public static string CleanContents(string responseContents)
        {
            return responseContents.Replace("\0", "").Replace(Request.BeginMarker, "").Replace(Request.EndOfMessage, "");
        }
    }

    /// <summary>
    /// Register team response
    /// </summary>
    public class RegisterTeamResponse : Response
    {
        /// <summary>
        /// Team ID
        /// </summary>
        public int TeamId;

        /// <summary>
        /// Expiration Time
        /// </summary>
        public string Expiration;
    }

    /// <summary>
    /// Unregister team response
    /// </summary>
    public class UnRegisterTeamResponse : Response
    {
    }

    /// <summary>
    /// Query team response
    /// </summary>
    public class QueryTeamResponse : Response
    {
    }

    /// <summary>
    /// Publish service response
    /// </summary>
    public class PublishServiceResponse : Response
    {
    }

    /// <summary>
    /// Query service response
    /// </summary>
    public class QueryServiceResponse : Response
    {
        /// <summary>
        /// Remote service
        /// </summary>
        public readonly RemoteService Service;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="service">Remote service</param>
        public QueryServiceResponse(RemoteService service)
        {
            this.Service = service;
        }
    }

    /// <summary>
    /// Execute service response
    /// </summary>
    public class ExecuteServiceResponse : Response
    {
        /// <summary>
        /// Return values
        /// </summary>
        public readonly RemoteServiceReturn Returned;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="returned">Return values</param>
        public ExecuteServiceResponse(RemoteServiceReturn returned)
        {
            this.Returned = returned;
        }
    }

    /// <summary>
    /// Failure response (in the case of an error)
    /// </summary>
    public class FailureResponse : Response
    {
        /// <summary>
        /// Error code
        /// </summary>
        public readonly string ErrorCode;

        /// <summary>
        /// Error message
        /// </summary>
        public readonly string ErrorMessage;

        /// <summary>
        /// Exception (if associated)
        /// </summary>
        public readonly FailureResponseException Exception;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="errorCode">Error code</param>
        /// <param name="errorMessage">Error message</param>
        public FailureResponse(string errorCode, string errorMessage)
        {
            this.ErrorCode = errorCode;
            this.ErrorMessage = errorMessage;
            this.Exception = new FailureResponseException(errorCode, errorMessage);
        }

        /// <summary>
        /// Convert to HL7
        /// </summary>
        /// <returns>HL7</returns>
        public string ToHl7()
        {
            var sb = new StringBuilder();
            sb.Append(String.Format("SOA|NOT-OK|{0}|{1}||{2}", ErrorCode, ErrorMessage, Request.NewRow));

            return Request.BeginMarker + sb.ToString() + Request.EndOfMessage;
        }

        /// <summary>
        /// Convert to HL7
        /// </summary>
        /// <returns>HL7</returns>
        public override string ToString()
        {
            return ToHl7();
        }
    }

    /// <summary>
    /// Exception that encapsulates a failure response
    /// </summary>
    public class FailureResponseException : Exception
    {
        /// <summary>
        /// Error code
        /// </summary>
        public readonly string ErrorCode;

        /// <summary>
        /// Error message
        /// </summary>
        public readonly string ErrorMessage;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="response">Failure response</param>
        public FailureResponseException(FailureResponse response) 
            : base(String.Format("Error: {0}, because {1}", response.ErrorCode, response.ErrorMessage))
        {
            this.ErrorCode = response.ErrorCode;
            this.ErrorMessage = response.ErrorMessage;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="errorCode">Error code</param>
        /// <param name="errorMessage">Error message</param>
        /// <param name="e">Exception (if associated)</param>
        public FailureResponseException(string errorCode, string errorMessage, Exception e = null)
            : base(String.Format("Error: {0}, because {1}", errorCode, errorMessage), e)
        {
            this.ErrorCode = errorCode;
            this.ErrorMessage = errorMessage;
        }
    }

    /// <summary>
    /// Invalid response type (could not match the message to a response type)
    /// </summary>
    public class InvalidResponseTypeException : Exception
    {
        /// <summary>
        /// </summary>
        public InvalidResponseTypeException()
        {
        }

        /// <summary>
        /// </summary>
        /// <param name="message">Error message</param>
        /// <param name="e">Base exception</param>
        public InvalidResponseTypeException(string message, Exception e): base(message, e)
        {
        }
    }

    /// <summary>
    /// Response factory, responsible for creating responses
    /// </summary>
    public class ResponseFactory
    {
        /// <summary>
        /// Is the response an non-failure?
        /// </summary>
        /// <param name="message">HL7</param>
        /// <returns></returns>
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

        /// <summary>
        /// Create a failure response from a message
        /// </summary>
        /// <param name="message">HL7</param>
        /// <returns></returns>
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

        /// <summary>
        /// Extract the rows of an HL7 message
        /// </summary>
        /// <param name="message">HL7</param>
        /// <returns></returns>
        private List<string[]> ExtractRows(string message)
        {
            var rows = new List<string[]>();
            var lines = message.Split(Request.NewRow.ToCharArray());
            foreach (var line in lines)
            {
                rows.Add(line.Split(Request.Delimiter.ToCharArray()));
            }

            return rows;
        }

        /// <summary>
        /// Create the response from a message and HL7 response content
        /// </summary>
        /// <param name="request">Request made</param>
        /// <param name="message">HL7 response content</param>
        /// <returns></returns>
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
                var description = rows[1][6];

                var mchIndex = 2 + numArgs + numResponses;
                var ip = rows[mchIndex][1];
                var port = int.Parse(rows[mchIndex][2]);
                var service = new RemoteService(IPAddress.Parse(ip), port, serviceName, fullRequest.TagName, 1, description);

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
                var name = rows[0][2];
                var numResponses = int.Parse(rows[0][4]);
                    
                var returnedCall = new RemoteServiceReturn(name);

                for (int resp = 0; resp < numResponses; resp++)
                {
                    var row = rows[1 + resp];
                    Debug.Assert(row[0] == "RSP");

                    var pos = int.Parse(row[1]);
                    var argName = row[2];
                    var dataType = row[3];
                    var value = row[4];

                    var serviceReturn = new ServiceReturn(pos, argName, ServiceArgument.TypeFromString(dataType), value);
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
