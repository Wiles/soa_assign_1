using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class HlConnection
    {
        public readonly string TeamName;

        private int teamId;
        public int TeamId
        {
            get { return teamId; }
        }

        private readonly IPAddress ip;
        private readonly int port;
        public HlConnection(string teamName)
        {
            this.TeamName = teamName;
            ip = IPAddress.Parse("127.0.0.1");
            port = 50020;
        }
    
        public HlConnection(string teamName, IPAddress ip, int port)
        {
            this.TeamName = teamName;
            this.ip = ip;
            this.port = port;
        }

        public RegisterTeamResponse Register()
        {
            throw new NotImplementedException();
        }

        public UnRegisterTeamResponse UnRegister()
        {
            throw new NotImplementedException();
        }

        public PublishServiceResponse Publish(HlService service)
        {
            throw new NotImplementedException();
        }

        public QueryTeamResponse QueryTeam(string p1, int teamId, string p2)
        {
            throw new NotImplementedException();
        }

        public QueryServiceResponse QueryService(string p)
        {
            throw new NotImplementedException();
        }

        public void ExecuteService(HlServiceCall call)
        {
            throw new NotImplementedException();
        }

        public bool IsRegistered()
        {
            // TODO: Double check with the server
            return String.IsNullOrWhiteSpace(TeamName) || teamId == null;
        }

    }
}
