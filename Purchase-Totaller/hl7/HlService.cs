using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Purchase_Totaller.hl7
{
    public class HlService
    {
        public readonly string Name;
        public readonly List<ServiceArgument> Args;
        public readonly ServiceReturn Return;

        public HlService(string name)
        {
            this.Name = name;
            this.Args = new List<ServiceArgument>();
            this.Return = new ServiceReturn();
        }
    }

    public class HlServiceCall
    {
        public readonly string Name;
        public readonly List<ServiceArgument> Args;

        public HlServiceCall(string name)
        {
            this.Name = name;
            Args = new List<ServiceArgument>();
        }
    }

    public class ServiceArgument
    {
        public int Position;
        public readonly string Name;
        public readonly ServiceArgumentType dataType;
        public readonly bool Mandatory;

        public ServiceArgument(int position, string name, string dataType, bool mandatory = false) :
            this(position, name, TypeFromString(dataType), mandatory)
        {
        }

        public ServiceArgument(int position, string name, ServiceArgumentType dataType, bool mandatory = false)
        {
            this.Position = position;
            this.Name = name;
            this.dataType = dataType;
            this.Mandatory = mandatory;
        }

        public static ServiceArgumentType TypeFromString(string dataType)
        {
            return (ServiceArgumentType)Enum.Parse(typeof(ServiceArgumentType), dataType.Substring(1));
        }
    }

    public enum ServiceArgumentType
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
    }
}
