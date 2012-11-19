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
            var connection = new HlConnection("Bob");
            connection.Register();

            var service = new LocalService(IPAddress.Parse("127.0.0.1"), 5453, "Service", "ServiceTag", 0, "Bob description");
            connection.Publish(service);
        }

        [TestMethod]
        public void TestRegisterTeam()
        {
            var connection = new HlConnection("Bob");
            var response = connection.Register();

            Assert.IsTrue(connection.TeamName == "Bob");
            Assert.IsTrue(connection.TeamId != null);
            Assert.IsTrue(connection.IsRegistered());
            Assert.IsTrue(response is RegisterTeamResponse);
        }

        [TestMethod]
        public void TestUnRegisterTeam()
        {
            var connection = new HlConnection("Bob");
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
            var connection = new HlConnection(teamname);
            int teamId = connection.Register().TeamId;

            var servicename = "Service1";
            var servicetag = "ServiceTag1";

            var service = new LocalService(IPAddress.Parse("127.0.0.1"), 5453, servicename, servicetag, 0, "Bob description");
            var arg = new ServiceArgument(0, "Name", ServiceDataType.Tstring, true);
            service.Args.Add(arg);

            var ret = new ServiceReturn(0, "TotalCount", ServiceDataType.Tint);
            service.Returns.Add(ret);

            connection.Publish(service);

            connection.QueryTeam(teamname, teamId, servicetag);
        }

        [TestMethod]
        public void QueryServiceMessage()
        {
            var connection = new HlConnection("Bob");
            connection.Register();

            connection.QueryService("Service1");
        }

        [TestMethod]
        public void ExecuteServiceMessage()
        {
            var connection = new HlConnection("Bob");
            connection.Register();

            var service = "test";

            var call = new RemoteServiceCall(service);

            var position = 0;
            var name = "test";
            var dataType = "int";
            call.Args.Add(new ServiceArgument(position, name, dataType));

            connection.ExecuteService(call);
        }
    }
}
