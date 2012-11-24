using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class Request
    {
        public readonly static string Delimiter = "|";
        public readonly static string NewRow = ((char)13).ToString();
        public readonly static string BeginMarker = ((char)11).ToString();
        public readonly static string EndMarker = ((char)28).ToString();
        public readonly static string EndOfMessage = new string(new char[] { ((char)28), ((char)13), ((char)10) });

        public readonly string Id;
        public readonly string TeamName;
        public readonly string TeamId;
        public readonly List<String> Contents;

        public Request(string id, string teamName, string teamId)
        {
            this.Id = id;
            this.TeamName = teamName;
            this.TeamId = teamId;

            Contents = new List<string>();

            Contents.Add("DRC");
            Contents.Add(id);
            Contents.Add(teamName);
            Contents.Add(teamId);
            Contents.Add(NewRow);
        }

        public string ToHl7()
        {
            var hl7 = String.Join(Delimiter, Contents).Replace(NewRow + Delimiter, NewRow);
            return BeginMarker + hl7 + EndMarker + NewRow;
        }

        public override string ToString()
        {
            return ToHl7();
        }
    }

    public class RegisterTeamRequest : Request
    {
        public RegisterTeamRequest(string teamname)
            : base("REG-TEAM", "", "")
        {
            Contents.Add("INF");
            Contents.Add(teamname);
            Contents.Add("");
            Contents.Add("");
            Contents.Add(NewRow);
        }
    }

    public class UnRegisterTeamRequest : Request
    {
        public UnRegisterTeamRequest(string teamName, int teamId)
            : base("UNREG-TEAM", teamName, teamId.ToString())
        {
        }
    }

    public class QueryTeamRequest : Request
    {
        public QueryTeamRequest(string teamName, int teamId,
            string queryTeamName, int queryTeamId, string serviceTag) :
            base("QUERY-TEAM", teamName, teamId.ToString())
        {
            Contents.Add("INF");
            Contents.Add(queryTeamName);
            Contents.Add(queryTeamId.ToString());
            Contents.Add(serviceTag);
            Contents.Add(NewRow);
        }
    }

    public class PublishServiceRequest : Request
    {
        public PublishServiceRequest(string teamName, int teamId, RemoteService service) :
            base("PUB-SERVICE", teamName, teamId.ToString())
        {
            Contents.Add("SRV");
            Contents.Add(service.Tag);
            Contents.Add(service.Name);
            Contents.Add(service.SecurityLevel.ToString());
            Contents.Add(service.Args.Count.ToString());
            Contents.Add(service.Returns.Count.ToString());
            Contents.Add(service.Description);
            Contents.Add(NewRow);

            foreach (var arg in service.Args)
            {
                Contents.Add("ARG");
                Contents.Add(arg.Position.ToString());
                Contents.Add(arg.Name);
                Contents.Add(ServiceArgument.TypeToString(arg.DataType));
                Contents.Add(arg.Mandatory ? "mandatory" : "optional");
                Contents.Add(NewRow);
            }

            foreach (var ret in service.Returns)
            {
                Contents.Add("RSP");
                Contents.Add(ret.Position.ToString());
                Contents.Add(ret.Name);
                Contents.Add(ServiceArgument.TypeToString(ret.DataType));
                Contents.Add(NewRow);
            }

            Contents.Add("MCH");
            Contents.Add(service.Ip.ToString());
            Contents.Add(service.Port.ToString());
            Contents.Add(NewRow);
        }
    }

    public class QueryServiceRequest : Request
    {
        public readonly string TagName;
        public QueryServiceRequest(string teamName, int teamId, string tagName) :
            base("QUERY-SERVICE", teamName, teamId.ToString())
        {
            this.TagName = tagName;

            Contents.Add("SRV");
            Contents.Add(tagName);
            Contents.Add("");
            Contents.Add("");
            Contents.Add("");
            Contents.Add("");
            Contents.Add("");
            Contents.Add(NewRow);
        }
    }

    public class ExecuteServiceRequest : Request
    {
        public ExecuteServiceRequest(string teamName, int teamId, RemoteServiceCall call) :
            base("EXEC-SERVICE", teamName, teamId.ToString())
        {
            Contents.Add("SRV");
            Contents.Add("");
            Contents.Add(call.ServiceName);
            Contents.Add("");
            Contents.Add(call.Args.Count.ToString());
            Contents.Add("");
            Contents.Add("");
            Contents.Add("");
            Contents.Add(NewRow);

            for (int i = 0; i < call.Args.Count; i++)
            {
                var arg = call.Args[i];
                Contents.Add("ARG");
                Contents.Add(arg.Position.ToString());
                Contents.Add(arg.Name);
                Contents.Add(ServiceArgument.TypeToString(arg.DataType));
                Contents.Add("");
                Contents.Add(arg.Value);
                Contents.Add(NewRow);
            }
        }
    }

    public class ExecuteServiceServerRequest
    {
        public readonly RemoteServiceCall Call;
        private ExecuteServiceServerRequest(RemoteServiceCall call)
        {
            this.Call = call;
        }

        public static ExecuteServiceServerRequest FromMessage(string received)
        {
            var lines = received.Split(Request.NewRow.ToCharArray());
            var rows = (from l in lines
                        select l.Split(Request.Delimiter.ToCharArray())).ToArray();

            var isOk = rows[0][1].Equals("ok", StringComparison.CurrentCultureIgnoreCase);
            if (isOk)
            {
                var serviceName = rows[1][2];
                var numSegments = int.Parse(rows[1][4]);

                var call = new RemoteServiceCall(serviceName);
                var request = new ExecuteServiceServerRequest(call);
                for (int i = 0; i < numSegments; i++)
                {
                    var row = rows[2 + i];
                    var pos = int.Parse(row[1]);
                    var respName = row[2];
                    var dataType = ServiceArgument.TypeFromString(row[3]);
                    var value = row[5];

                    var arg = new ServiceArgument(pos, respName, dataType);
                    arg.Value = value;
                    call.Args.Add(arg);
                }

                return request;
            }
            else
            {
                throw new FailureRequestException("-6", "");
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class ExecuteServiceServerResponse
    {
        public ExecuteServiceServerResponse(double purchaseAmount, string province)
        {
        }

        public string ToHl7()
        {
            throw new NotImplementedException();
        }
    }

    public class FailureRequestException : Exception
    {
        public readonly string ErrorCode;
        public readonly string ErrorMessage;
        public FailureRequestException(string errorCode, string errorMessage)
        {
            this.ErrorCode = errorCode;
            this.ErrorMessage = errorMessage;
        }
    }
}
