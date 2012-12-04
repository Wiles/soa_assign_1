using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Shared
{
    public class Logger
    {
        /// <summary>
        /// The singleton instance
        /// </summary>
        private static Logger instance;

        /// <summary>
        /// Gets the singleton instance.
        /// </summary>
        /// <returns>The singleton instance</returns>
        public static Logger GetInstance(string name = "")
        {
            if (instance == null)
            {
                instance = new Logger(String.Format(@"C:\temp\log{0}.txt", name));
            }

            return instance;
        }

        /// <summary>
        /// The path
        /// </summary>
        private readonly string Path;

        /// <summary>
        /// The mutex
        /// </summary>
        private readonly Mutex mutex = new Mutex();

        /// <summary>
        /// Prevents a default instance of the <see cref="Logger" /> class from being created.
        /// </summary>
        /// <param name="path">The path.</param>
        private Logger(string path)
        {
            this.Path = path;
        }

        /// <summary>
        /// Writes the specified message.
        /// </summary>
        /// <param name="message">The message.</param>
        public void Write(string message, int indent = 0)
        {
            try
            {
                if (mutex.WaitOne())
                {
                    var writer = new StreamWriter(Path, true);
                    writer.WriteLine(DateTime.Now.ToString() + " " + message);
                    writer.Close();

                    mutex.ReleaseMutex();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Failure to write to log, because " + ex.Message);
                Console.WriteLine(DateTime.Now.ToString() + " " + message);
            }
        }

        /// <summary>
        /// Writes the specified message.
        /// </summary>
        /// <param name="message">The message.</param>
        /// <param name="args">The args.</param>
        public void Write(string message, params object[] args)
        {
            Write(String.Format(message, args));
        }

        /// <summary>
        /// Writes the specified exception.
        /// </summary>
        /// <param name="ex">The ex.</param>
        public void Write(Exception ex)
        {
            Write(ex, ex.Message, new object[] { });
        }

        /// <summary>
        /// Writes the specified exception.
        /// </summary>
        /// <param name="ex">The ex.</param>
        /// <param name="message">The message.</param>
        /// <param name="args">The args.</param>
        public void Write(Exception ex, string message, params object[] args)
        {
            Write(String.Format(message, args));
            Write(ex.ToString());
        }
    }
}
