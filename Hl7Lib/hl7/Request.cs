using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Hl7Lib
{
    /// <summary>
    /// HL7 request
    /// </summary>
    public class Request
    {
        /// <summary>
        /// Delimiter for a field
        /// </summary>
        public readonly static string Delimiter = "|";

        /// <summary>
        /// New row character sequence
        /// </summary>
        public readonly static string NewRow = ((char)13).ToString();

        /// <summary>
        /// Beginning of message character sequence
        /// </summary>
        public readonly static string BeginMarker = ((char)11).ToString();

        /// <summary>
        /// End of message marker
        /// </summary>
        public readonly static string EndMarker = ((char)28).ToString();

        /// <summary>
        /// End of message character sequence
        /// </summary>
        public readonly static string EndOfMessage = new string(new char[] { ((char)13), ((char)28), ((char)13) });

        /// <summary>
        /// ID of message
        /// </summary>
        public readonly string Id;

        /// <summary>
        /// Team name
        /// </summary>
        public readonly string TeamName;

        /// <summary>
        /// Team id
        /// </summary>
        public readonly string TeamId;

        /// <summary>
        /// Contents of message in rows
        /// </summary>
        public readonly List<String> Contents;

        /// <summary>
        /// Create a request for the following team name and id
        /// </summary>
        /// <param name="id">ID of message</param>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
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

        /// <summary>
        /// Convert request to HL7
        /// </summary>
        /// <returns>HL7</returns>
        public string ToHl7()
        {
            var hl7 = String.Join(Delimiter, Contents).Replace(NewRow + Delimiter, NewRow);
            return BeginMarker + hl7 + EndMarker + NewRow;
        }

        /// <summary>
        /// Convert request to HL7
        /// </summary>
        /// <returns>HL7</returns>
        public override string ToString()
        {
            return ToHl7();
        }
    }

    /// <summary>
    /// Request team request
    /// </summary>
    public class RegisterTeamRequest : Request
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamname">Team name</param>
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

    /// <summary>
    /// Unregister team request
    /// </summary>
    public class UnRegisterTeamRequest : Request
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
        public UnRegisterTeamRequest(string teamName, int teamId)
            : base("UNREG-TEAM", teamName, teamId.ToString())
        {
        }
    }

    /// <summary>
    /// Query team request
    /// </summary>
    public class QueryTeamRequest : Request
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
        /// <param name="queryTeamName">Team name to query</param>
        /// <param name="queryTeamId">Team id to query</param>
        /// <param name="serviceTag">Service tag to query</param>
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

    /// <summary>
    /// Publish service request
    /// </summary>
    public class PublishServiceRequest : Request
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
        /// <param name="service">Service</param>
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

    /// <summary>
    /// Query service request
    /// </summary>
    public class QueryServiceRequest : Request
    {
        /// <summary>
        /// Tag name of the service
        /// </summary>
        public readonly string TagName;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
        /// <param name="tagName">Service tag</param>
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

    /// <summary>
    /// Execute service request
    /// </summary>
    public class ExecuteServiceRequest : Request
    {
        /// <summary>
        /// 
        /// </summary>
        /// <param name="teamName">Team name</param>
        /// <param name="teamId">Team id</param>
        /// <param name="call">Remote service call</param>
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

    /// <summary>
    /// Request coming from client to execute a service on a service machine
    /// </summary>
    public class ExecuteServiceServerRequest
    {
        /// <summary>
        /// Remote service call
        /// </summary>
        public readonly RemoteServiceCall Call;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="call">Remote service call</param>
        private ExecuteServiceServerRequest(RemoteServiceCall call)
        {
            this.Call = call;
        }

        /// <summary>
        /// Create the request from HL7
        /// </summary>
        /// <param name="received"></param>
        /// <returns></returns>
        public static ExecuteServiceServerRequest FromMessage(string received)
        {
            var lines = received.Split(Request.NewRow.ToCharArray());
            var rows = (from l in lines
                        select l.Split(Request.Delimiter.ToCharArray())).ToArray();

            var teamName = rows[0][2];
            var teamId = int.Parse(rows[0][3]);
            var serviceName = rows[1][2];
            var numSegments = int.Parse(rows[1][4]);

            var call = new RemoteServiceCall(serviceName, teamName, teamId);
            var request = new ExecuteServiceServerRequest(call);

            // Read the arguments
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
    }

    /// <summary>
    /// Response for the purchase totaller service
    /// </summary>
    public class PurchaseTotallerResponse
    {
        public static readonly ServiceReturn SubTotalReturn = new ServiceReturn(1, "SubTotal", ServiceDataType.Tdouble);
        public static readonly ServiceReturn PstReturn = new ServiceReturn(2, "PST", ServiceDataType.Tdouble);
        public static readonly ServiceReturn HstReturn = new ServiceReturn(3, "HST", ServiceDataType.Tdouble);
        public static readonly ServiceReturn GstReturn = new ServiceReturn(4, "GST", ServiceDataType.Tdouble);
        public static readonly ServiceReturn TotalReturn = new ServiceReturn(5, "Total", ServiceDataType.Tdouble);

        /// <summary>
        /// Canadian provincial tax rate
        /// </summary>
        private class ProvinceTaxRate
        {
            public readonly string Code;
            public readonly string Province;
            public readonly double Hst;
            public readonly double Pst;
            public readonly double Gst;

            public ProvinceTaxRate(string code, string province, double hst, double pst, double gst)
            {
                this.Code = code;
                this.Province = province;
                this.Hst = hst;
                this.Pst = pst;
                this.Gst = gst;
            }

        }

        /// <summary>
        /// Tax rates for canadian provinces
        /// </summary>
        private readonly List<ProvinceTaxRate> taxRates = new List<ProvinceTaxRate>(new ProvinceTaxRate[]
        {
            new ProvinceTaxRate("NL", "Newfoundland", 13.0, 0.0, 0.0),
            new ProvinceTaxRate("NS", "Nova Scotia", 15.0, 0.0, 0.0),
            new ProvinceTaxRate("NB", "New Brunswick", 13.0, 0.0, 0.0),
            new ProvinceTaxRate("PE", "Prince Edward Island", 0.0, 10.0, 5.0),
            new ProvinceTaxRate("QC", "Quebec", 0.0, 9.5, 5.0),
            new ProvinceTaxRate("ON", "Ontario", 13.0, 0.0, 0.0),
            new ProvinceTaxRate("MB", "Manitoba", 0.0, 7.0, 5.0),
            new ProvinceTaxRate("SK", "Saskatchewan", 0.0, 5.0, 5.0),
            new ProvinceTaxRate("AB", "Albert", 0.0, 0.0, 5.0),
            new ProvinceTaxRate("BC", "British Columbia", 12.0, 0.0, 0.0),
            new ProvinceTaxRate("YT", "Yukon", 0.0, 0.0, 5.0),
            new ProvinceTaxRate("NT", "Northwest Territories", 0.0, 0.0, 5.0),
            new ProvinceTaxRate("NU", "Nunavut", 0.0, 0.0, 5.0)
        });

        /// <summary>
        /// Code name of province
        /// </summary>
        private readonly string provinceCode;

        /// <summary>
        /// Amount of purchase
        /// </summary>
        private readonly double purchaseAmount;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="provinceCode">Code name of province</param>
        /// <param name="purchaseAmount">Amount of purchase</param>
        public PurchaseTotallerResponse(string provinceCode, double purchaseAmount)
        {
            this.provinceCode = provinceCode;
            this.purchaseAmount = purchaseAmount;
        }

        /// <summary>
        /// Create HL7 from response
        /// </summary>
        /// <returns>HL7</returns>
        public string ToHl7()
        {
            var taxRate = (from t in taxRates
                           where t.Code == provinceCode
                           select t).FirstOrDefault();

            if (taxRate == null)
            {
                throw new Exception("Unknown provinceCode == " + provinceCode);
            }

            // Perform the calculations
            var subtotal = purchaseAmount;
            var hst = (taxRate.Hst / 100) * subtotal;
            var gst = (taxRate.Gst / 100) * subtotal;
            var pst = 0.0;
            if (provinceCode == "QC" || provinceCode == "PE")
            {
                pst = (taxRate.Pst / 100) * (subtotal + gst);
            }
            else
            {
                pst = (taxRate.Pst / 100) * subtotal;
            }

            var total = subtotal + pst + hst + gst;

            // Create the return values
            var sr = SubTotalReturn;
            sr.Value = subtotal;

            var pr = PstReturn;
            pr.Value = pst;

            var hr = HstReturn;
            hr.Value = hst;

            var gr = GstReturn;
            gr.Value = gst;

            var tr = TotalReturn;
            tr.Value = total;

            // Create HL7
            var sb = new StringBuilder();
            sb.Append(String.Format("PUB|OK|||{0}|{1}", 5, Request.NewRow));
            foreach (var ret in new ServiceReturn[] { sr, pr, hr, gr, tr })
            {
                sb.Append(String.Format("RSP|{0}|{1}|{2}|{3}|{4}",
                    ret.Position, ret.Name, ServiceArgument.TypeToString(ret.DataType), ret.Value, Request.NewRow));
            }

            return Request.BeginMarker + sb.ToString() + Request.EndOfMessage;
        }

        /// <summary>
        /// Create HL7 from response
        /// </summary>
        /// <returns>HL7</returns>
        public override string ToString()
        {
            return ToHl7();
        }
    }

    /// <summary>
    /// Failure response received (turn it into a request)
    /// </summary>
    public class FailureRequestException : Exception
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
        /// <param name="errorCode">Error code</param>
        /// <param name="errorMessage">Error message</param>
        public FailureRequestException(string errorCode, string errorMessage) :
            base(String.Format("Error: {0}, {1}", errorCode, errorMessage))
        {
            this.ErrorCode = errorCode;
            this.ErrorMessage = errorMessage;
        }
    }
}
