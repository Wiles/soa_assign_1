using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Purchase_Totaller.hl7;

namespace tests
{
    [TestClass]
    public class Connection
    {
        [TestMethod]
        public void TestPublish()
        {
            var connection = new HlConnection("Bob");
            connection.Register();

            var service = new HlService("ServiceTag");
            connection.Publish(service);
        }

        [TestMethod]
        public void TestRegister()
        {
            var connection = new HlConnection("Bob");
            connection.Register();
        }

        [TestMethod]
        public void TestUnRegister()
        {
            var connection = new HlConnection("Bob");
            int teamId = connection.Register().TeamId;

            // TODO: Assert registered

            connection.UnRegister();
        }

        [TestMethod]
        public void TestQueryTeam()
        {
            var connection = new HlConnection("Bob");
            int teamId = connection.Register().TeamId;

            connection.QueryTeam("Bob", teamId, "Service1");
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

            var call = new HlServiceCall(service);

            var position = 0;
            var name = "test";
            var dataType = "int";
            call.Args.Add(new ServiceArgument(position, name, dataType));

            connection.ExecuteService(call);
        }
    }
}
