using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class LocalService
    {
        public readonly IPAddress Ip;
        public readonly int Port;
        public readonly string Name;
        public readonly string Tag;
        public readonly int SecurityLevel;
        public readonly string Description;
        public readonly List<ServiceArgument> Args;
        public readonly List<ServiceReturn> Returns;

        public LocalService(IPAddress ip, int port, string name, string tag, int securityLevel = 1, string description = "")
        {
            this.Ip = ip;
            this.Port = port;
            this.Name = name;
            this.Tag = tag;
            this.SecurityLevel = securityLevel;
            this.Description = description;
            this.Args = new List<ServiceArgument>();
            this.Returns = new List<ServiceReturn>();
        }
    }

    public class RemoteServiceCall
    {
        public readonly string ServiceName;
        public readonly List<ServiceArgument> Args;

        public RemoteServiceCall(string serviceName)
        {
            this.ServiceName = serviceName;
            Args = new List<ServiceArgument>();
        }
    }

    public class RemoteServiceReturn
    {
        public readonly string Name;
        public readonly List<ServiceArgument> Args;
        public readonly List<ServiceReturn> Returns;

        public RemoteServiceReturn(string name)
        {
            this.Name = name;
            this.Args = new List<ServiceArgument>();
            this.Returns = new List<ServiceReturn>();
        }
    }

    public class ServiceArgument
    {
        public int Position;
        public readonly string Name;
        public readonly ServiceDataType dataType;
        public readonly bool Mandatory;

        public string Value;

        public ServiceArgument(int position, string name, string dataType, bool mandatory = false) :
            this(position, name, TypeFromString(dataType), mandatory)
        {
        }

        public ServiceArgument(int position, string name, ServiceDataType dataType, bool mandatory = false)
        {
            this.Position = position;
            this.Name = name;
            this.dataType = dataType;
            this.Mandatory = mandatory;
        }

        public static ServiceDataType TypeFromString(string dataType)
        {
            return (ServiceDataType)Enum.Parse(typeof(ServiceDataType), "T" + dataType.ToLower());
        }

        public static string TypeToString(ServiceDataType dataType)
        {
            return dataType.ToString().Substring(1);
        }
    }

    public enum ServiceDataType
    {
        Tint,
        Tdouble,
        Tstring,
        Tfloat,
        Tchar,
        Tshort,
        Tlong
    }

    public class ServiceReturn
    {
        public readonly int Position;
        public readonly string Name;
        public readonly ServiceDataType DataType;

        public ServiceReturn(int pos, string name, ServiceDataType dataType)
        {
            this.Position = pos;
            this.Name = name;
            this.DataType = dataType;
        }
    }
}
