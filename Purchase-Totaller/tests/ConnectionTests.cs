using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Purchase_Totaller.hl7;
using System.Net;

namespace tests
{
    [TestClass]
    public class ConnectionTests
    {
        [TestMethod]
        public void TestPublish()
        {
            var connection = new ServiceConnection("Bob");
            connection.Register();

            var service = new RemoteService(IPAddress.Parse("127.0.0.1"), 5453,
                "Service", "GIORP-TOTAL", 1, "Bob description");

            var arg = new ServiceArgument(1, "x", ServiceDataType.Tstring, true);
            service.Args.Add(arg);

            var ret = new ServiceReturn(1, "Bob", ServiceDataType.Tstring);
            service.Returns.Add(ret);

            connection.Publish(service);
        }

        [TestMethod]
        public void TestRegisterTeam()
        {
            var connection = new ServiceConnection("Bob");
            var response = connection.Register();

            Assert.IsTrue(connection.TeamName == "Bob");
            Assert.IsTrue(connection.TeamId != null);
            Assert.IsTrue(connection.IsRegistered());
            Assert.IsTrue(response is RegisterTeamResponse);
        }

        [TestMethod]
        public void TestUnRegisterTeam()
        {
            var connection = new ServiceConnection("Bob");
            connection.Register();

            var response = connection.UnRegister();
            Assert.IsTrue(response is UnRegisterTeamResponse);
            Assert.IsTrue(connection.TeamId == null);
            Assert.IsTrue(!connection.IsRegistered());
        }

        [TestMethod]
        public void TestQueryTeam()
        {
            var teamname = "Bob";
            var connection = new ServiceConnection(teamname);
            int teamId = connection.Register().TeamId;

            var serviceName = "Service1";
            var serviceTag = "GIORP-TOTAL";

            var service = new RemoteService(IPAddress.Parse("127.0.0.1"), 5453, 
                serviceName, serviceTag, 1, "Bob description");
            var arg = new ServiceArgument(1, "Name", ServiceDataType.Tstring, true);
            service.Args.Add(arg);

            var ret = new ServiceReturn(1, "TotalCount", ServiceDataType.Tint);
            service.Returns.Add(ret);

            connection.Publish(service);

            connection.QueryTeam(teamname, teamId, serviceTag);
        }

        [TestMethod]
        public void QueryServiceMessage()
        {
            var connection = new ServiceConnection("Bob");
            connection.Register();

            connection.QueryService("GIORP-TOTAL");
        }

        [TestMethod]
        public void ExecuteServiceMessage()
        {
            var connection = new ServiceConnection("Bob");
            connection.Register();

            var service = "GIORP-TOTAL";

            var call = new RemoteServiceCall(service);

            var position = 1;
            var name = "test";
            var dataType = "int";
            call.Args.Add(new ServiceArgument(position, name, dataType));

            connection.ExecuteService(call);
        }
    }
}
