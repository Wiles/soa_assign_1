using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class Response
    {
        public string status;
        public string errorCode;
        public string errorMessage;
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

    public class FailureResponseException: Exception
    {
        public FailureResponseException(string errorCode, string errorMessage, Exception e = null): base
            (String.Format("Error: {0}, because {1}", errorCode, errorMessage), e)
        {
        }
    }

}
