using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace Hl7Lib
{
    /// <summary>
    /// Service on local machine with return values
    /// </summary>
    public class LocalService
    {
        /// <summary>
        /// Name of service
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// Return values
        /// </summary>
        public readonly List<ServiceReturn> Returns;


        /// <summary>
        /// 
        /// </summary>
        /// <param name="name">Name of service</param>
        public LocalService(string name)
        {
            this.Name = name;
            this.Returns = new List<ServiceReturn>();
        }
    }

    /// <summary>
    /// Service on a remote machine
    /// </summary>
    public class RemoteService
    {
        /// <summary>
        /// IP Address of remote service
        /// </summary>
        public readonly IPAddress Ip;

        /// <summary>
        /// Port number
        /// </summary>
        public readonly int Port;

        /// <summary>
        /// Service name
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// Service tag
        /// </summary>
        public readonly string Tag;

        /// <summary>
        /// Security level (1 = low, 2 = medium, 3 = high)
        /// </summary>
        public readonly int SecurityLevel;

        /// <summary>
        /// Description of service
        /// </summary>
        public readonly string Description;

        /// <summary>
        /// Service arguments
        /// </summary>
        public readonly List<ServiceArgument> Args;

        /// <summary>
        /// Service returns
        /// </summary>
        public readonly List<ServiceReturn> Returns;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="ip">IP of machine hosting remote service</param>
        /// <param name="port">Port of machine hosting remote service connection</param>
        /// <param name="name">Service name</param>
        /// <param name="tag">Service Tag</param>
        /// <param name="securityLevel">Security level (1 = low, 2 = medium, 3 = high)</param>
        /// <param name="description">Description of service</param>
        public RemoteService(IPAddress ip, int port, string name, 
            string tag, int securityLevel = 1, string description = "")
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

    /// <summary>
    /// Call to a remote service
    /// </summary>
    public class RemoteServiceCall
    {
        /// <summary>
        /// Team name of caller
        /// </summary>
        public readonly string CallerTeamName;

        /// <summary>
        /// Team id of caller
        /// </summary>
        public readonly int CallerTeamId;

        /// <summary>
        /// Service name to execute
        /// </summary>
        public readonly string ServiceName;

        /// <summary>
        /// Arguments
        /// </summary>
        public readonly List<ServiceArgument> Args;

        /// <summary>
        /// Return values
        /// </summary>
        public readonly List<ServiceReturn> Returns;

        /// <summary>
        /// </summary>
        /// <param name="serviceName">Service name</param>
        /// <param name="callerTeamName">Caller team name</param>
        /// <param name="callerTeamId">Caller team id</param>
        public RemoteServiceCall(string serviceName, string callerTeamName = "", int callerTeamId = 0)
        {
            this.ServiceName = serviceName;
            this.CallerTeamId = callerTeamId;
            this.CallerTeamName = callerTeamName;
            this.Args = new List<ServiceArgument>();
            this.Returns = new List<ServiceReturn>();
        }
    }

    /// <summary>
    /// Return values and arguments for a remote service
    /// </summary>
    public class RemoteServiceReturn
    {
        /// <summary>
        /// Remote service name
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// Arguments
        /// </summary>
        public readonly List<ServiceArgument> Args;

        /// <summary>
        /// Return values
        /// </summary>
        public readonly List<ServiceReturn> Returns;

        /// <summary>
        /// </summary>
        /// <param name="name">Service name</param>
        public RemoteServiceReturn(string name)
        {
            this.Name = name;
            this.Args = new List<ServiceArgument>();
            this.Returns = new List<ServiceReturn>();
        }
    }

    /// <summary>
    /// Service argument
    /// </summary>
    public class ServiceArgument
    {
        /// <summary>
        /// Position of argument
        /// </summary>
        public int Position;

        /// <summary>
        /// Name of argument
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// Datatype of argument
        /// </summary>
        public readonly ServiceDataType DataType;

        /// <summary>
        /// Whether argument is mandatory or not
        /// </summary>
        public readonly bool Mandatory;

        /// <summary>
        /// Value of argument
        /// </summary>
        public string Value;

        /// <summary>
        /// 
        /// </summary>
        /// <param name="position">Position of arguments (starts at 1)</param>
        /// <param name="name">Name of argument</param>
        /// <param name="dataType">Datatype of argument</param>
        /// <param name="mandatory">Whether the argument is mandatory or not</param>
        public ServiceArgument(int position, string name, string dataType, bool mandatory = false) :
            this(position, name, TypeFromString(dataType), mandatory)
        {
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="position">Position of arguments (starts at 1)</param>
        /// <param name="name">Name of argument</param>
        /// <param name="dataType">Datatype of argument</param>
        /// <param name="mandatory">Whether the argument is mandatory or not</param>
        /// <param name="value">Value of argument</param>
        public ServiceArgument(int position, string name, ServiceDataType dataType, 
            bool mandatory = false, string value = "")
        {
            this.Position = position;
            this.Name = name;
            this.DataType = dataType;
            this.Mandatory = mandatory;
            this.Value = value;
        }

        /// <summary>
        /// Create datatype from string
        /// </summary>
        /// <param name="dataType">
        /// int
        /// char
        /// string
        /// double
        /// float
        /// short
        /// long
        /// </param>
        /// <returns>Datatype</returns>
        public static ServiceDataType TypeFromString(string dataType)
        {
            return (ServiceDataType)Enum.Parse(typeof(ServiceDataType), "T" + dataType.ToLower());
        }

        /// <summary>
        /// Return the datatype as a string
        /// </summary>
        /// <param name="dataType">datatype</param>
        /// <returns>
        /// int
        /// char
        /// string
        /// double
        /// float
        /// short
        /// long
        /// </returns>
        public static string TypeToString(ServiceDataType dataType)
        {
            return dataType.ToString().Substring(1);
        }
    }

    /// <summary>
    /// Service datatype used in arguments and return values
    /// </summary>
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

    /// <summary>
    /// Service return value
    /// </summary>
    public struct ServiceReturn
    {
        /// <summary>
        /// Position of return value (starts at 1)
        /// </summary>
        public readonly int Position;

        /// <summary>
        /// Name of return value
        /// </summary>
        public readonly string Name;

        /// <summary>
        /// Datatype of return value
        /// </summary>
        public readonly ServiceDataType DataType;

        /// <summary>
        /// Value
        /// </summary>
        private object value;

        /// <summary>
        /// Value
        /// </summary>
        public object Value
        {
            get
            {
                if (value == null)
                {
                    return "";
                }
                else
                {
                    return value.ToString();
                }
            }
            set
            {
                this.value = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="pos">Position of return value (starts at 1)</param>
        /// <param name="name">Name of return value</param>
        /// <param name="dataType">Datatype of return value</param>
        /// <param name="value">Value of return value</param>
        public ServiceReturn(int pos, string name, ServiceDataType dataType, string value = "")
        {
            this.Position = pos;
            this.Name = name;
            this.DataType = dataType;
            this.value = value;
        }
    }
}
