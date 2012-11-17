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

        public override string ToString()
        {
            return BeginMarker + String.Join(Delimiter, Contents) + EndMarker + NewRow;
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
        public PublishServiceRequest(string teamName, int teamId):
            base("PUB-SERVICE", teamName, teamId.ToString())
        {
            throw new NotImplementedException();
        }
    }

    public class QueryServiceRequest : Request
    {
        public QueryServiceRequest(string teamName, int teamId, string tagName): 
            base("QUERY-SERVICE", teamName, teamId.ToString())
        {
            Contents.Add("SRV");
            Contents.Add(tagName);
            Contents.Add(NewRow);
        }
    }

    public class ExecuteServiceRequest : Request
    {
        public ExecuteServiceRequest(string teamName, int teamId, HlServiceCall call):
            base("EXEC-SERVICE", teamName, teamId.ToString())
        {
            throw new NotImplementedException();
        }
    }
}
