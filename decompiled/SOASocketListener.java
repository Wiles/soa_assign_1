/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.UnknownHostException;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Properties;
/*      */ 
/*      */ class SOASocketListener extends Thread
/*      */ {
/* 2927 */   private final int INFO = 1;
/* 2928 */   private final int DEBUG = 0;
/* 2929 */   private final int UNDEFINED = 0;
/* 2930 */   private final int REG_TEAM = 1;
/* 2931 */   private final int UNREG_TEAM = 2;
/* 2932 */   private final int QUERY_TEAM = 3;
/* 2933 */   private final int PUB_SERVICE = 4;
/* 2934 */   private final int QUERY_SERVICE = 5;
/* 2935 */   private final int MIN_SEC_LEVEL = 1;
/* 2936 */   private final int MAX_SEC_LEVEL = 3;
/* 2937 */   private final String fieldSeparator = "|";
/*      */   private Connection dbase;
/*      */   private boolean errOnUnreachIP;
/*      */   private boolean errOnUnconnectedPort;
/*      */   private boolean teamTestOwn;
/*      */   private int msgCount;
/*      */   private int msgSocket;
/*      */   private int errorCode;
/*      */   private int whichCommand;
/*      */   private int timeout;
/*      */   private int timeoutMessageCount;
/*      */   private int numSpooledMessages;
/*      */   private char startMsgChar;
/*      */   private char endMsgChar;
/*      */   private char endSegmentChar;
/*      */   private String msgSuccess;
/*      */   private String msgFailure;
/* 2953 */   private boolean isTestMode = false;
/* 2954 */   private boolean terminateOnNACK = false;
/* 2955 */   private boolean isFirstSegment = false;
/*      */   private String msgReady;
/*      */   private String msgBye;
/*      */   private String nameOfFeedingSystem;
/*      */   private String currSegment;
/*      */   private String finalGoodOutputDirectory;
/*      */   private String finalBadOutputDirectory;
/*      */   private String tempOutputDirectory;
/*      */   private String currMsgResponse;
/*      */   private String errorMessage;
/*      */   private String okContent;
/*      */   private String okBody;
/*      */   private String teamName;
/*      */   private String teamID;
/*      */   private String tagName;
/*      */   private String teamExpire;
/*      */   private String launchDate;
/*      */   private String dbaseDriver;
/*      */   private String dbaseURL;
/*      */   private String dbaseUser;
/*      */   private String dbasePasswd;
/*      */   private String spooledMessage;
/*      */   private Properties soaListenerProperties;
/* 2978 */   private SimpleDateFormat FORMAT_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 2979 */   private SimpleDateFormat FILENAME_GENERATOR = new SimpleDateFormat("yyyyMMddHHmmssSS");
/*      */ 
/*      */   public SOASocketListener(String paramString)
/*      */   {
/*   37 */     SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
/*      */ 
/*   40 */     this.msgCount = 0;
/*      */ 
/*   43 */     this.soaListenerProperties = new Properties();
/*      */     try
/*      */     {
/*   46 */       String str1 = paramString + "\\soa.msgListener.properties";
/*      */ 
/*   48 */       FileInputStream localFileInputStream = new FileInputStream(str1);
/*   49 */       this.soaListenerProperties.load(localFileInputStream);
/*   50 */       localFileInputStream.close();
/*      */ 
/*   53 */       this.launchDate = localSimpleDateFormat.format(new Date());
/*      */     }
/*      */     catch (Exception localException1)
/*      */     {
/*   57 */       getClass(); LogMsg(localException1, "SOASocketListener : Error while accessing the SOA properties file (soa.msgListener.properties)", 1);
/*      */     }
/*      */ 
/*   61 */     String str2 = this.soaListenerProperties.getProperty("IncomingListenerPort", "3128");
/*      */     try
/*      */     {
/*   64 */       this.msgSocket = Integer.parseInt(str2);
/*      */     }
/*      */     catch (Exception localException2)
/*      */     {
/*   68 */       this.msgSocket = 3128;
/*      */     }
/*      */ 
/*   73 */     String str3 = this.soaListenerProperties.getProperty("TeamTimeout", "NEVER");
/*   74 */     if (str3.compareTo("NEVER") == 0)
/*      */     {
/*   76 */       this.timeout = -1;
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*   82 */         this.timeout = Integer.parseInt(str3);
/*      */       }
/*      */       catch (Exception localException3)
/*      */       {
/*   86 */         this.timeout = 5;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*   91 */     str3 = this.soaListenerProperties.getProperty("CheckTimeoutAfterNumMessages", "10");
/*      */     try
/*      */     {
/*   94 */       this.timeoutMessageCount = Integer.parseInt(str3);
/*      */     }
/*      */     catch (Exception localException4)
/*      */     {
/*   98 */       this.timeoutMessageCount = 10;
/*      */     }
/*      */ 
/*  102 */     this.teamTestOwn = false;
/*  103 */     str3 = this.soaListenerProperties.getProperty("TeamTestOwn", "NO");
/*  104 */     if (str3.compareTo("YES") == 0) this.teamTestOwn = true;
/*      */ 
/*  108 */     this.errOnUnreachIP = false;
/*  109 */     str3 = this.soaListenerProperties.getProperty("ErrOnUnreachableIP", "NO");
/*  110 */     if (str3.compareTo("YES") == 0) this.errOnUnreachIP = true;
/*      */ 
/*  113 */     this.errOnUnconnectedPort = false;
/*  114 */     str3 = this.soaListenerProperties.getProperty("ErrOnNonActivePublishLocation", "NO");
/*  115 */     if (str3.compareTo("YES") == 0) this.errOnUnconnectedPort = true;
/*      */ 
/*  118 */     this.nameOfFeedingSystem = this.soaListenerProperties.getProperty("NameOfSOASystem", "Misys");
/*      */ 
/*  122 */     this.msgReady = this.soaListenerProperties.getProperty("ReadyForNewMessageMessage", "NONE");
/*      */ 
/*  125 */     this.msgBye = this.soaListenerProperties.getProperty("ClientTerminatingMessage", "NONE");
/*      */ 
/*  128 */     this.finalGoodOutputDirectory = this.soaListenerProperties.getProperty("SOAListenerGoodOutputDir", ".\\Good\\");
/*  129 */     this.finalBadOutputDirectory = this.soaListenerProperties.getProperty("SOAListenerBadOutputDir", ".\\Bad\\");
/*  130 */     this.tempOutputDirectory = this.soaListenerProperties.getProperty("SOAListenerTemporaryDir", ".\\");
/*      */ 
/*  133 */     this.msgSuccess = this.soaListenerProperties.getProperty("MessageReceived", "AA");
/*      */ 
/*  135 */     this.msgFailure = this.soaListenerProperties.getProperty("MessageNotReceived", "AE");
/*      */ 
/*  141 */     this.startMsgChar = '\013';
/*      */ 
/*  145 */     this.endMsgChar = '\034';
/*      */ 
/*  149 */     this.endSegmentChar = '\r';
/*      */ 
/*  152 */     str3 = this.soaListenerProperties.getProperty("TestMessageMode", "NO");
/*  153 */     if (str3.compareTo("YES") == 0) this.isTestMode = true;
/*      */ 
/*  156 */     str3 = this.soaListenerProperties.getProperty("TerminateOnNACK", "NO");
/*  157 */     if (str3.compareTo("YES") == 0) this.terminateOnNACK = true;
/*      */ 
/*  160 */     str3 = this.soaListenerProperties.getProperty("SOAListenerLogDir", "NO");
/*      */ 
/*  164 */     this.dbaseDriver = this.soaListenerProperties.getProperty("SOAListenerJDBCDriver", "NO-JDBC");
/*  165 */     this.dbaseURL = this.soaListenerProperties.getProperty("SOAListenerDBaseURL", "NO-DBASE");
/*  166 */     this.dbaseUser = this.soaListenerProperties.getProperty("SOAListenerDBaseUser", "NO-DBASE-USER");
/*  167 */     this.dbasePasswd = this.soaListenerProperties.getProperty("SOAListenerDBasePasswd", "NO-DBASE-PASSWD");
/*      */   }
/*      */ 
/*      */   public void run()
/*      */   {
/*  179 */     whoStartedMe();
/*      */ 
/*  182 */     while (startListen() > 0);
/*  185 */     System.exit(-1);
/*      */   }
/*      */ 
/*      */   public void whoStartedMe()
/*      */   {
/*  197 */     SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
/*      */ 
/*  199 */     String str2 = "";
/*  200 */     String str3 = this.soaListenerProperties.getProperty("SOAListenerLogRollover", "NO");
/*      */ 
/*  202 */     if (str3.compareTo("YES") == 0)
/*      */     {
/*  204 */       Date localDate = new Date();
/*  205 */       int i = localDate.getYear() + 1900;
/*  206 */       int j = localDate.getMonth() + 1;
/*  207 */       int k = localDate.getDate();
/*  208 */       int m = localDate.getHours();
/*  209 */       int n = localDate.getMinutes();
/*      */ 
/*  213 */       getClass(); LogMsg(null, "SOASocketListener : Rolling Over LISTENER Log", 1);
/*  214 */       m -= 1;
/*      */ 
/*  217 */       m += 24;
/*  218 */       k -= 1;
/*  219 */       if (k == 0)
/*      */       {
/*  221 */         int i1 = 31;
/*  222 */         getClass(); LogMsg(null, "SOASocketListener :   ==> Back to the Previous Month", 1);
/*  223 */         j -= 1;
/*  224 */         if (j == 0) j = 12;
/*      */ 
/*  226 */         if ((j == 9) || (j == 4) || (j == 6) || (j == 11)) i1 = 30;
/*  227 */         if (j == 2)
/*      */         {
/*  229 */           int i2 = 0;
/*  230 */           if (i % 4 == 0) i2 = 1;
/*  231 */           if (i % 100 == 0) i2 = 0;
/*  232 */           if (i % 2000 == 0) i2 = 1;
/*  233 */           i1 = 28;
/*  234 */           if (i2 == 1) i1 = 29;
/*      */         }
/*  236 */         k += i1;
/*  237 */         if (j == 12) i -= 1;
/*      */ 
/*      */       }
/*      */ 
/*  241 */       str2 = this.soaListenerProperties.getProperty("SOAListenerLogDir", "../logs/") + "\\SOARegisterListener.log";
/*  242 */       str2 = str2 + "." + Integer.toString(i) + "-";
/*  243 */       if (j < 10) str2 = str2 + "0";
/*  244 */       str2 = str2 + Integer.toString(j) + "-";
/*  245 */       if (k < 10) str2 = str2 + "0";
/*  246 */       str2 = str2 + Integer.toString(k);
/*      */ 
/*  248 */       String str1 = this.soaListenerProperties.getProperty("SOAListenerLogDir", "../logs/") + "\\SOARegisterListenerYesterday.log";
/*  249 */       File localFile = new File(str1);
/*  250 */       boolean bool = localFile.renameTo(new File(str2));
/*  251 */       if (!bool) { getClass(); LogMsg(null, "SOASocketListener : Log File Rename Failed", 1); }
/*  252 */       if (bool == true) { getClass(); LogMsg(null, "SOASocketListener : Log File Rename Passed", 1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void openConnection()
/*      */     throws ClassNotFoundException, SQLException
/*      */   {
/*  266 */     Class.forName(this.dbaseDriver);
/*      */ 
/*  268 */     this.dbase = DriverManager.getConnection(this.dbaseURL, this.dbaseUser, this.dbasePasswd);
/*      */   }
/*      */ 
/*      */   protected Connection getDBase()
/*      */   {
/*  277 */     return this.dbase;
/*      */   }
/*      */ 
/*      */   private void openDBase()
/*      */   {
/*      */     try
/*      */     {
/*  287 */       openConnection();
/*  288 */       getDBase().setAutoCommit(false);
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException)
/*      */     {
/*  292 */       getClass(); LogMsg(null, "openDBase  :  >> Error while loading JDBC Driver [" + localClassNotFoundException.getMessage() + "]", 1);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  296 */       getClass(); LogMsg(null, "openDBase  :  >> Error while establishing DBase connection [" + localSQLException.getMessage() + "]", 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void closeDBase(boolean paramBoolean)
/*      */   {
/*  305 */     if (paramBoolean)
/*      */     {
/*      */       try
/*      */       {
/*  309 */         getDBase().commit();
/*      */       }
/*      */       catch (SQLException localSQLException2)
/*      */       {
/*  313 */         getClass(); LogMsg(null, "closeDBase :  >> Error during Commmit [" + localSQLException2.getMessage() + "]", 1);
/*      */       }
/*      */       finally
/*      */       {
/*      */         try
/*      */         {
/*  319 */           getDBase().close();
/*      */         }
/*      */         catch (SQLException localSQLException7)
/*      */         {
/*  323 */           getClass(); LogMsg(null, "closeDBase :  >> Error during Close [" + localSQLException7.getMessage() + "]", 1);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*  331 */         getDBase().rollback();
/*      */       }
/*      */       catch (SQLException localSQLException5)
/*      */       {
/*  335 */         getClass(); LogMsg(null, "closeDBase :  >> Error during Rollback [" + localSQLException5.getMessage() + "]", 1);
/*      */       }
/*      */       finally
/*      */       {
/*      */         try
/*      */         {
/*  341 */           getDBase().close();
/*      */         }
/*      */         catch (SQLException localSQLException8)
/*      */         {
/*  345 */           getClass(); LogMsg(null, "closeDBase :  >> Error during Close [" + localSQLException8.getMessage() + "]", 1);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int startListen()
/*      */   {
/*  358 */     int i = 1;
/*      */ 
/*  361 */     int k = 1;
/*  362 */     boolean bool = true;
/*      */ 
/*  367 */     Socket localSocket = null;
/*      */ 
/*  373 */     char[] arrayOfChar = new char[1];
/*  374 */     arrayOfChar[0] = this.endSegmentChar;
/*      */ 
/*  376 */     getClass(); LogMsg(null, "-----------------------------------------------", 1);
/*  377 */     getClass(); LogMsg(null, "   SOA Register Online ... " + this.launchDate + " ", 1);
/*  378 */     getClass(); LogMsg(null, "-----------------------------------------------", 1);
/*  379 */     getClass(); LogMsg(null, "  >>> SOARegisterListener Active - Listening for messages from " + this.nameOfFeedingSystem + " on Socket:" + this.msgSocket, 1);
/*      */     try
/*      */     {
/*  382 */       ServerSocket localServerSocket = new ServerSocket(this.msgSocket);
/*  383 */       if (!this.isTestMode)
/*      */       {
/*  385 */         localServerSocket.setReuseAddress(true);
/*      */       }
/*      */ 
/*  389 */       localSocket = localServerSocket.accept();
/*  390 */       if (!this.isTestMode)
/*      */       {
/*  392 */         localSocket.setKeepAlive(true);
/*      */       }
/*      */ 
/*  395 */       BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/*  396 */       PrintWriter localPrintWriter = new PrintWriter(localSocket.getOutputStream(), true);
/*      */ 
/*  399 */       while (k != 0)
/*      */       {
/*      */         try
/*      */         {
/*  403 */           getClass(); LogMsg(null, "startListen :       >> Rx/Tx Streams OPEN - (notDone=" + bool + ")", 1);
/*  404 */           String str1 = localBufferedReader.readLine();
/*      */           int m;
/*  407 */           if ((this.timeout > 0) && (this.msgCount % this.timeoutMessageCount == 0))
/*      */           {
/*  409 */             m = checkForTimeouts();
/*  410 */             if (m > 0) { getClass(); LogMsg(null, "startListen :         >> Found and removed " + m + " expired team(s)", 1);
/*      */             }
/*      */           }
/*      */ 
/*  414 */           if (str1 == null)
/*      */           {
/*  416 */             getClass(); LogMsg(null, "startListen :         >> Failed to READ from socket - endOfStream reached", 1);
/*      */           }
/*      */           else
/*      */           {
/*  423 */             if (this.endSegmentChar == '\r')
/*      */             {
/*  425 */               getClass(); LogMsg(null, "startListen :         >> Spooling Incoming Message", 1);
/*      */ 
/*  428 */               m = str1.length();
/*      */ 
/*  430 */               getClass(); LogMsg(null, "startListen :           >> Appending MsgSegment : (" + str1.charAt(0) + "," + str1.charAt(1) + "," + str1.charAt(2) + "," + str1.charAt(3) + " ... " + str1.charAt(m - 3) + "," + str1.charAt(m - 2) + "," + str1.charAt(m - 1) + ")", 0);
/*  431 */               str1 = str1 + new String(arrayOfChar);
/*  432 */               while (bool)
/*      */               {
/*  434 */                 String str2 = localBufferedReader.readLine();
/*  435 */                 int j = str2.length() - 1;
/*      */ 
/*  438 */                 m = str2.length();
/*      */ 
/*  441 */                 if (str2.charAt(j) == this.endMsgChar)
/*      */                 {
/*  443 */                   if (m < 3)
/*      */                   {
/*  445 */                     getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : length =" + m, 0);
/*  446 */                     if (m == 1) { getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : (" + str2.charAt(0) + ")", 0); }
/*  447 */                     if (m == 2) { getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : (" + str2.charAt(0) + "," + str2.charAt(1) + ")", 0); }
/*      */                   }
/*      */                   else
/*      */                   {
/*  451 */                     getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : (" + str2.charAt(0) + "," + str2.charAt(1) + " ... " + str2.charAt(m - 2) + "," + str2.charAt(m - 1) + ")", 0);
/*      */                   }
/*  453 */                   str1 = str1 + str2;
/*  454 */                   bool = false;
/*      */                 }
/*      */                 else
/*      */                 {
/*  458 */                   if (m < 3)
/*      */                   {
/*  460 */                     getClass(); LogMsg(null, "startListen :           >> Appending Next MsgSegment : length =" + m, 0);
/*  461 */                     if (m == 1) { getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : (" + str2.charAt(0) + ")", 0); }
/*  462 */                     if (m == 2) { getClass(); LogMsg(null, "startListen :           >> Appending Last MsgSegment : (" + str2.charAt(0) + "," + str2.charAt(1) + ")", 0); }
/*      */                   }
/*      */                   else
/*      */                   {
/*  466 */                     getClass(); LogMsg(null, "startListen :           >> Appending Next MsgSegment : (" + str2.charAt(0) + "," + str2.charAt(1) + " ... " + str2.charAt(m - 2) + "," + str2.charAt(m - 1) + ")", 0);
/*      */                   }
/*  468 */                   str1 = str1 + str2 + new String(arrayOfChar);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  473 */             getClass(); LogMsg(null, "startListen :         >> Processing Data", 1);
/*      */ 
/*  476 */             if (str1.charAt(0) == this.startMsgChar)
/*      */             {
/*  478 */               i = processMessage(str1);
/*      */             }
/*      */             else
/*      */             {
/*  482 */               getClass(); LogMsg(null, "startListen :           >> First Byte of Message is not a <StartOfMessage>", 1);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  487 */           if (str1 != null)
/*      */           {
/*  489 */             if (i == 0)
/*      */             {
/*  491 */               getClass(); LogMsg(null, "startListen :           >> Message NOT RECEIVED", 1);
/*  492 */               localPrintWriter.println(this.currMsgResponse.toString());
/*      */ 
/*  494 */               localPrintWriter.flush();
/*  495 */               if (this.isTestMode)
/*      */               {
/*  497 */                 k = 0;
/*  498 */                 i = 0;
/*      */               }
/*  500 */               if (this.terminateOnNACK)
/*      */               {
/*  502 */                 k = 0;
/*  503 */                 i = 0;
/*      */               }
/*      */               else
/*      */               {
/*  507 */                 k = 1;
/*  508 */                 i = 1;
/*      */               }
/*      */             }
/*  511 */             else if ((this.msgReady.compareTo("NONE") != 0) && (str1.indexOf(this.msgReady) >= 0))
/*      */             {
/*  513 */               getClass(); LogMsg(null, "startListen :           >> " + this.nameOfFeedingSystem + " checking if READY (Received \"msgReady\" message at index " + str1.indexOf(this.msgReady) + " of the incoming stream", 1);
/*  514 */               localPrintWriter.println(this.msgSuccess);
/*      */ 
/*  516 */               localPrintWriter.flush();
/*      */             }
/*  518 */             else if ((str1.indexOf(this.msgBye) < 0) || (this.msgBye.compareTo("NONE") == 0))
/*      */             {
/*  520 */               getClass(); LogMsg(null, "startListen :           >> Sending response Message from " + this.nameOfFeedingSystem, 1);
/*  521 */               getClass(); LogMsg(null, "startListen :              >> Response is ... \n------------------------------\n" + this.currMsgResponse.substring(1, this.currMsgResponse.length() - 2) + "\n------------------------------", 0);
/*  522 */               this.msgCount += 1;
/*      */ 
/*  530 */               localPrintWriter.println(this.currMsgResponse);
/*      */ 
/*  532 */               localPrintWriter.flush();
/*      */             }
/*      */             else
/*      */             {
/*  536 */               getClass(); LogMsg(null, "startListen :           >> " + this.nameOfFeedingSystem + " sent TERMINATE message", 1);
/*  537 */               localPrintWriter.println(this.msgFailure);
/*      */ 
/*  539 */               localPrintWriter.flush();
/*  540 */               k = 0;
/*  541 */               i = 0;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  546 */             getClass(); LogMsg(null, "startListen :           >> Incoming Message IS NULL", 1);
/*  547 */             localPrintWriter.println(this.msgFailure);
/*      */ 
/*  549 */             localPrintWriter.flush();
/*  550 */             if (this.isTestMode)
/*      */             {
/*  552 */               k = 0;
/*  553 */               i = 0;
/*      */             }
/*      */ 
/*  557 */             if (this.terminateOnNACK)
/*      */             {
/*  559 */               k = 0;
/*  560 */               i = 0;
/*      */             }
/*      */             else
/*      */             {
/*  564 */               k = 1;
/*  565 */               i = 1;
/*      */             }
/*      */           }
/*      */ 
/*  569 */           if (k == 0)
/*      */           {
/*  571 */             getClass(); LogMsg(null, "startListen :         >> Closing Rx/Tx Streams on this Message", 1);
/*  572 */             localPrintWriter.close();
/*  573 */             localBufferedReader.close();
/*  574 */             if (this.isTestMode)
/*      */             {
/*  576 */               localSocket.close();
/*  577 */               localServerSocket.close();
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  582 */             bool = true;
/*      */           }
/*      */         }
/*      */         catch (Exception localException1)
/*      */         {
/*  587 */           getClass(); LogMsg(localException1, "startListen :     >> **EXCEPTION**", 1);
/*      */           try
/*      */           {
/*  591 */             getClass(); LogMsg(null, "startListen :    >> Processing Exception - closing socket and reopening", 1);
/*  592 */             localPrintWriter.close();
/*  593 */             localBufferedReader.close();
/*  594 */             localSocket.close();
/*  595 */             localServerSocket.close();
/*      */ 
/*  597 */             localServerSocket = new ServerSocket(this.msgSocket);
/*  598 */             if (!this.isTestMode)
/*      */             {
/*  600 */               localServerSocket.setReuseAddress(true);
/*      */             }
/*  602 */             localSocket = localServerSocket.accept();
/*  603 */             if (!this.isTestMode)
/*      */             {
/*  605 */               localSocket.setKeepAlive(true);
/*      */             }
/*  607 */             localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/*  608 */             localPrintWriter = new PrintWriter(localSocket.getOutputStream(), true);
/*      */ 
/*  610 */             k = 1;
/*  611 */             i = 1;
/*      */           }
/*      */           catch (Exception localException3)
/*      */           {
/*  615 */             getClass(); LogMsg(localException3, "startListen :    >> Unable to close socket - further exception - exiting listener", 1);
/*  616 */             k = 0;
/*  617 */             i = 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  622 */         localPrintWriter.close();
/*  623 */         localBufferedReader.close();
/*  624 */         localSocket.close();
/*  625 */         localServerSocket.close();
/*      */ 
/*  627 */         localServerSocket = new ServerSocket(this.msgSocket);
/*  628 */         if (!this.isTestMode)
/*      */         {
/*  630 */           localServerSocket.setReuseAddress(true);
/*      */         }
/*  632 */         localSocket = localServerSocket.accept();
/*  633 */         if (!this.isTestMode)
/*      */         {
/*  635 */           localSocket.setKeepAlive(true);
/*      */         }
/*  637 */         localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/*  638 */         localPrintWriter = new PrintWriter(localSocket.getOutputStream(), true);
/*      */       }
/*      */ 
/*  643 */       getClass(); LogMsg(null, "  >>> SOARegisterListener Inactive", 1);
/*  644 */       if (!this.isTestMode)
/*      */       {
/*  646 */         localSocket.close();
/*  647 */         localServerSocket.close();
/*      */       }
/*      */     }
/*      */     catch (Exception localException2)
/*      */     {
/*  652 */       getClass(); LogMsg(localException2, "startListen :     >> **EXCEPTION**", 1);
/*      */       try
/*      */       {
/*  656 */         getClass(); LogMsg(null, "startListener :    >> Processing Exception - closing socket - exiting listener", 1);
/*  657 */         i = 0;
/*  658 */         if (localSocket != null) localSocket.close();
/*      */       }
/*      */       catch (Exception localException4)
/*      */       {
/*  662 */         getClass(); LogMsg(localException4, "startListen :    >> Unable to close socket - further exception - exiting listener", 1);
/*  663 */         i = 0;
/*      */       }
/*      */     }
/*      */ 
/*  667 */     return i;
/*      */   }
/*      */ 
/*      */   private String getDateAfterXMinutes(int paramInt)
/*      */   {
/*  676 */     Calendar localCalendar = Calendar.getInstance();
/*      */ 
/*  678 */     if (paramInt != 0) localCalendar.add(12, paramInt);
/*  679 */     String str = localCalendar.get(1) + "-";
/*  680 */     if (localCalendar.get(2) + 1 < 10)
/*      */     {
/*  682 */       str = str + "0" + (localCalendar.get(2) + 1) + "-";
/*      */     }
/*      */     else
/*      */     {
/*  686 */       str = str + (localCalendar.get(2) + 1) + "-";
/*      */     }
/*  688 */     if (localCalendar.get(5) < 10)
/*      */     {
/*  690 */       str = str + "0" + localCalendar.get(5);
/*      */     }
/*      */     else
/*      */     {
/*  694 */       str = str + localCalendar.get(5);
/*      */     }
/*      */ 
/*  697 */     return str;
/*      */   }
/*      */ 
/*      */   private String getTimeAfterXMinutes(int paramInt)
/*      */   {
/*  706 */     Calendar localCalendar = Calendar.getInstance();
/*      */ 
/*  708 */     if (paramInt != 0) localCalendar.add(12, paramInt);
/*      */     String str;
/*  709 */     if (localCalendar.get(11) < 10)
/*      */     {
/*  711 */       str = "0" + localCalendar.get(11) + ":";
/*      */     }
/*      */     else
/*      */     {
/*  715 */       str = localCalendar.get(11) + ":";
/*      */     }
/*  717 */     if (localCalendar.get(12) < 10)
/*      */     {
/*  719 */       str = str + "0" + localCalendar.get(12) + ":";
/*      */     }
/*      */     else
/*      */     {
/*  723 */       str = str + localCalendar.get(12) + ":";
/*      */     }
/*  725 */     if (localCalendar.get(13) < 10)
/*      */     {
/*  727 */       str = str + "0" + localCalendar.get(13);
/*      */     }
/*      */     else
/*      */     {
/*  731 */       str = str + localCalendar.get(13);
/*      */     }
/*      */ 
/*  734 */     return str;
/*      */   }
/*      */ 
/*      */   private int checkForTimeouts()
/*      */   {
/*  742 */     int i = 0;
/*      */ 
/*  747 */     String str1 = "";
/*  748 */     String str2 = getDateAfterXMinutes(0);
/*  749 */     String str3 = getTimeAfterXMinutes(0);
/*      */ 
/*  752 */     getClass(); LogMsg(null, "checkForTimeouts :       >> Checking for team time-outs - anything before " + str2 + " at " + str3 + ".", 1);
/*  753 */     openDBase();
/*      */     try
/*      */     {
/*  758 */       str1 = "select teamID, teamName from Team where expirationDate<'" + str2 + "' or (expirationDate='" + str2 + "' and expirationTime <'" + str3 + "') order by teamID;";
/*  759 */       PreparedStatement localPreparedStatement1 = getDBase().prepareStatement(str1);
/*  760 */       localPreparedStatement1.execute();
/*  761 */       ResultSet localResultSet = localPreparedStatement1.getResultSet();
/*  762 */       while (localResultSet.next())
/*      */       {
/*  764 */         i++;
/*  765 */         int j = Integer.parseInt(localResultSet.getString(1));
/*  766 */         String str4 = localResultSet.getString(2);
/*  767 */         getClass(); LogMsg(null, "checkForTimeouts :          >> Team '" + str4 + "' (ID : " + j + ") has expired ... removing registration and services ...", 1);
/*      */ 
/*  770 */         str1 = "delete from Response where serviceID in (select serviceID from Service where teamID=" + j + ");";
/*  771 */         PreparedStatement localPreparedStatement2 = getDBase().prepareStatement(str1);
/*  772 */         localPreparedStatement2.execute();
/*  773 */         int k = localPreparedStatement2.getUpdateCount();
/*  774 */         getClass(); LogMsg(null, "checkForTimeouts :            >> Removed " + k + " row(s) from the Response table belonging to team", 0);
/*  775 */         localPreparedStatement2.close();
/*      */ 
/*  777 */         str1 = "delete from Argument where serviceID in (select serviceID from Service where teamID=" + j + ");";
/*  778 */         localPreparedStatement2 = getDBase().prepareStatement(str1);
/*  779 */         localPreparedStatement2.execute();
/*  780 */         k = localPreparedStatement2.getUpdateCount();
/*  781 */         getClass(); LogMsg(null, "checkForTimeouts :            >> Removed " + k + " row(s) from the Argument table belonging to team", 0);
/*  782 */         localPreparedStatement2.close();
/*      */ 
/*  784 */         str1 = "delete from Service where teamID=" + j + ";";
/*  785 */         localPreparedStatement2 = getDBase().prepareStatement(str1);
/*  786 */         localPreparedStatement2.execute();
/*  787 */         k = localPreparedStatement2.getUpdateCount();
/*  788 */         getClass(); LogMsg(null, "checkForTimeouts :            >> Removed " + k + " row(s) from the Service table belonging to team", 0);
/*  789 */         localPreparedStatement2.close();
/*      */ 
/*  791 */         str1 = "delete from Team where teamID=" + j + ";";
/*  792 */         localPreparedStatement2 = getDBase().prepareStatement(str1);
/*  793 */         localPreparedStatement2.execute();
/*  794 */         k = localPreparedStatement2.getUpdateCount();
/*  795 */         getClass(); LogMsg(null, "checkForTimeouts :            >> Removed " + k + " row(s) from the Team table belonging to team", 0);
/*  796 */         localPreparedStatement2.close();
/*      */       }
/*  798 */       localPreparedStatement1.close();
/*  799 */       closeDBase(true);
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/*  803 */       getClass(); LogMsg(null, "checkForTimeouts :                >> Error executing SQL=[" + str1 + "] - error=[" + localSQLException.getMessage() + "]", 1);
/*  804 */       this.errorMessage = ("Error executing SQL=[" + str1 + "] - error=[" + localSQLException.getMessage() + "]");
/*  805 */       this.errorCode = -5;
/*  806 */       closeDBase(false);
/*      */     }
/*      */ 
/*  810 */     return i;
/*      */   }
/*      */ 
/*      */   private boolean isValidTagName(String paramString)
/*      */   {
/*  818 */     boolean bool = false;
/*      */ 
/*  820 */     if ((paramString.compareTo("GIORP-TOTAL") == 0) || (paramString.compareTo("PAYROLL") == 0) || (paramString.compareTo("CAR-LOAN") == 0) || (paramString.compareTo("POSTAL") == 0)) bool = true;
/*      */ 
/*  822 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isAlphaNumeric(String paramString)
/*      */   {
/*  830 */     boolean bool = true;
/*      */ 
/*  834 */     for (int i = 0; i < paramString.length(); i++)
/*      */     {
/*  836 */       int j = paramString.charAt(i);
/*  837 */       if (j < 48) bool = false;
/*  838 */       if ((j > 57) && (j < 65)) bool = false;
/*  839 */       if ((j > 90) && (j < 97)) bool = false;
/*  840 */       if (j > 122) bool = false;
/*      */     }
/*  842 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidDatatype(String paramString)
/*      */   {
/*  850 */     boolean bool = false;
/*      */ 
/*  852 */     if (paramString.compareToIgnoreCase("char") == 0) bool = true;
/*  853 */     if (paramString.compareToIgnoreCase("int") == 0) bool = true;
/*  854 */     if (paramString.compareToIgnoreCase("short") == 0) bool = true;
/*  855 */     if (paramString.compareToIgnoreCase("long") == 0) bool = true;
/*  856 */     if (paramString.compareToIgnoreCase("float") == 0) bool = true;
/*  857 */     if (paramString.compareToIgnoreCase("double") == 0) bool = true;
/*  858 */     if (paramString.compareToIgnoreCase("String") == 0) bool = true;
/*      */ 
/*  860 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidIPFormat(String paramString)
/*      */   {
/*  868 */     boolean bool = false;
/*      */ 
/*  871 */     bool = paramString.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}");
/*  872 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidIPAddress(String paramString)
/*      */   {
/*  880 */     boolean bool = false;
/*      */     try
/*      */     {
/*  885 */       InetAddress localInetAddress = InetAddress.getByName(paramString);
/*      */ 
/*  887 */       bool = localInetAddress.isReachable(2000);
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException)
/*      */     {
/*  891 */       getClass(); LogMsg(null, "processPubService :                  >> IP Address " + paramString + " is unreachable - " + localUnknownHostException, 1);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  895 */       getClass(); LogMsg(null, "processPubService :                  >> IP Address " + paramString + " is unreachable - " + localIOException, 1);
/*      */     }
/*  897 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidPublishLocation(String paramString1, String paramString2)
/*      */   {
/*  905 */     boolean bool = false;
/*      */     try
/*      */     {
/*  911 */       InetAddress localInetAddress = InetAddress.getByName(paramString1);
/*  912 */       int i = Integer.parseInt(paramString2);
/*      */ 
/*  914 */       Socket localSocket = new Socket(localInetAddress, i);
/*  915 */       localSocket.close();
/*      */ 
/*  917 */       bool = true;
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException)
/*      */     {
/*  921 */       getClass(); LogMsg(null, "processPubService :                  >> Service Published on " + paramString1 + ":" + paramString2 + " is unreachable - " + localUnknownHostException, 1);
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  925 */       getClass(); LogMsg(null, "processPubService :                  >> Service Published on " + paramString1 + ":" + paramString2 + " is unreachable - " + localIOException, 1);
/*      */     }
/*  927 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidArgtype(String paramString)
/*      */   {
/*  935 */     boolean bool = false;
/*      */ 
/*  937 */     if (paramString.compareToIgnoreCase("mandatory") == 0) bool = true;
/*  938 */     if (paramString.compareToIgnoreCase("optional") == 0) bool = true;
/*      */ 
/*  940 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isValidCommentCharacters(String paramString)
/*      */   {
/*  948 */     boolean bool = true;
/*      */ 
/*  952 */     for (int i = 0; i < paramString.length(); i++)
/*      */     {
/*  954 */       int j = paramString.charAt(i);
/*  955 */       if (j < 32) bool = false;
/*  956 */       if ((j > 33) && (j < 36)) bool = false;
/*  957 */       if ((j > 36) && (j < 40)) bool = false;
/*  958 */       if ((j > 41) && (j < 44)) bool = false;
/*  959 */       if (j == 45) bool = false;
/*  960 */       if ((j > 58) && (j < 65)) bool = false;
/*  961 */       if (j > 122) bool = false;
/*      */     }
/*  963 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean isSecLevelValid(String paramString)
/*      */   {
/*  971 */     boolean bool = true;
/*      */ 
/*  973 */     if ((paramString.compareTo("1") != 0) && (paramString.compareTo("2") != 0) && (paramString.compareTo("3") != 0)) bool = false;
/*      */ 
/*  975 */     return bool;
/*      */   }
/*      */ 
/*      */   private boolean inValidRange(String paramString, int paramInt1, int paramInt2)
/*      */   {
/*  983 */     boolean bool = true;
/*      */     try
/*      */     {
/*  988 */       int i = Integer.parseInt(paramString);
/*  989 */       if (paramInt1 <= paramInt2)
/*      */       {
/*  991 */         if ((i < paramInt1) || (i > paramInt2)) bool = false;
/*      */ 
/*      */       }
/*  995 */       else if (i < paramInt1) bool = false;
/*      */ 
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/* 1000 */       bool = false;
/*      */     }
/* 1002 */     return bool; } 
/* 1011 */   private int processMessage(String paramString) { int i = 0;
/*      */ 
/* 1013 */     int m = 1;
/*      */ 
/* 1015 */     PrintWriter localPrintWriter = null;
/*      */ 
/* 1019 */     char[] arrayOfChar1 = new char[1];
/* 1020 */     arrayOfChar1[0] = this.endSegmentChar;
/* 1021 */     char[] arrayOfChar2 = new char[1];
/* 1022 */     arrayOfChar2[0] = this.startMsgChar;
/* 1023 */     char[] arrayOfChar3 = new char[1];
/* 1024 */     arrayOfChar3[0] = this.endMsgChar;
/*      */ 
/* 1027 */     this.whichCommand = 0;
/* 1028 */     this.isFirstSegment = true;
/* 1029 */     this.currMsgResponse = "";
/* 1030 */     this.errorMessage = "";
/* 1031 */     this.errorCode = 0;
/* 1032 */     this.okContent = "";
/* 1033 */     this.okBody = "";
/*      */ 
/* 1035 */     getClass(); LogMsg(null, "processMessage :           >> Processing Incoming Message", 1);
/*      */ 
/* 1037 */     int j = 1;
/*      */     String str1;
/*      */     try { if (this.isFirstSegment == true)
/*      */       {
/* 1043 */         str1 = this.tempOutputDirectory + "\\currentMsg.soa";
/* 1044 */         localPrintWriter = new PrintWriter(new FileWriter(str1, false));
/*      */ 
/* 1047 */         getClass(); LogMsg(null, "processMessage :              >> Opening DBase ...", 0);
/* 1048 */         openDBase();
/*      */       }
/*      */ 
/* 1051 */       while (j >= 0)
/*      */       {
/* 1053 */         int k = paramString.indexOf(this.endSegmentChar, j);
/* 1054 */         if (k <= j)
/*      */         {
/* 1057 */           j = -1;
/*      */ 
/* 1060 */           if (this.currMsgResponse.length() == 0)
/*      */           {
/* 1063 */             getClass(); LogMsg(null, "processMessage :               >> Failed to find first segment", 1);
/*      */           }
/*      */           else
/*      */           {
/* 1068 */             getClass(); LogMsg(null, "processMessage :               >> Failed to find subsequent segment (" + m + ")", 1);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1074 */           this.currSegment = paramString.substring(j, k);
/* 1075 */           getClass(); LogMsg(null, "processMessage :               >> Parsed out segment (" + this.currSegment + ")", 1);
/* 1076 */           localPrintWriter.println(this.currSegment);
/* 1077 */           localPrintWriter.flush();
/*      */ 
/* 1081 */           i += ParseSegment();
/*      */ 
/* 1085 */           if (paramString.length() <= k + 1)
/*      */           {
/* 1087 */             getClass(); LogMsg(null, "processMessage :                 >> Message doesn't contain EOM marker", 1);
/* 1088 */             j = -1;
/* 1089 */             this.errorMessage = "Message doesn't contain EOM marker";
/* 1090 */             this.errorCode = -1;
/* 1091 */             i += this.errorCode;
/*      */           }
/* 1097 */           else if (paramString.charAt(k + 1) == this.endMsgChar)
/*      */           {
/* 1099 */             getClass(); LogMsg(null, "processMessage :                 >> found EOM marker", 1);
/* 1100 */             j = -1;
/*      */           }
/*      */           else
/*      */           {
/* 1104 */             j = k + 1;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/* 1112 */       getClass(); LogMsg(localIOException, "parseSegment :             >> Unable to open TEMPORARY file for writing", 1);
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1116 */       getClass(); LogMsg(localException, "processData :           ** EXCEPTION**", 1);
/*      */     }
/*      */ 
/* 1120 */     localPrintWriter.flush();
/* 1121 */     localPrintWriter.close();
/*      */     String str2;
/*      */     File localFile;
/*      */     boolean bool;
/* 1126 */     if (i == 0)
/*      */     {
/* 1128 */       i = 1;
/* 1129 */       getClass(); LogMsg(null, "processMessage :               >> Incoming SOA Message Saved to Temporary File", 1);
/*      */ 
/* 1132 */       str1 = this.tempOutputDirectory + "\\currentMsg.soa";
/* 1133 */       str2 = this.finalGoodOutputDirectory + "\\" + this.FILENAME_GENERATOR.format(new Date(System.currentTimeMillis())) + ".soa";
/* 1134 */       localFile = new File(str1);
/* 1135 */       bool = localFile.renameTo(new File(str2));
/* 1136 */       if (!bool) { getClass(); LogMsg(null, "processMessage :                 >> File (" + str1 + ") not renamed to (" + str2 + ")", 1); }
/* 1137 */       if (bool == true) { getClass(); LogMsg(null, "processMessage :                 >> File Renamed to (" + str2 + ")", 1);
/*      */       }
/*      */ 
/* 1140 */       this.currMsgResponse = (new String(arrayOfChar2) + "SOA|OK|" + this.okContent + new String(arrayOfChar1));
/* 1141 */       if (this.okBody.length() > 0)
/*      */       {
/* 1143 */         this.currMsgResponse += this.okBody;
/*      */       }
/* 1145 */       this.currMsgResponse += new String(arrayOfChar3);
/*      */ 
/* 1147 */       getClass(); LogMsg(null, "processMessage :                 >> Constructing OK Response", 1);
/*      */ 
/* 1150 */       getClass(); LogMsg(null, "processMessage :                 >> Committing DBase transaction(s)", 1);
/* 1151 */       closeDBase(true);
/*      */     }
/*      */     else
/*      */     {
/* 1155 */       i = 0;
/* 1156 */       getClass(); LogMsg(null, "processMessage :               >> Incoming SOA Message FAILED to be Saved to Temporary File - moving to BAD location", 1);
/*      */ 
/* 1159 */       str1 = this.tempOutputDirectory + "\\currentMsg.soa";
/* 1160 */       str2 = this.finalBadOutputDirectory + "\\" + this.FILENAME_GENERATOR.format(new Date(System.currentTimeMillis())) + ".soa";
/* 1161 */       localFile = new File(str1);
/* 1162 */       bool = localFile.renameTo(new File(str2));
/* 1163 */       if (!bool) { getClass(); LogMsg(null, "processMessage :                 >> File (" + str1 + ") not renamed to (" + str2 + ")", 1); }
/* 1164 */       if (bool == true) { getClass(); LogMsg(null, "processMessage :                 >> File Renamed to (" + str2 + ")", 1);
/*      */       }
/*      */ 
/* 1167 */       this.currMsgResponse = (new String(arrayOfChar2) + "SOA|NOT-OK|" + Integer.toString(this.errorCode) + "|" + this.errorMessage + "|" + new String(arrayOfChar1) + new String(arrayOfChar3));
/* 1168 */       getClass(); LogMsg(null, "processMessage :               >> Constructing NOT-OK Response", 1);
/* 1169 */       getClass(); LogMsg(null, "processMessage :                 >> (.. |" + Integer.toString(this.errorCode) + "|" + this.errorMessage + "| ..", 1);
/*      */ 
/* 1172 */       getClass(); LogMsg(null, "processMessage :                 >> Rolling Back DBase transaction(s)", 1);
/* 1173 */       closeDBase(false);
/*      */     }
/*      */ 
/* 1177 */     getClass(); LogMsg(null, "processMessage :           >> Processing Complete", 0);
/* 1178 */     return i;
/*      */   }
/*      */ 
/*      */   private int ParseSegment()
/*      */   {
/* 1187 */     int i = 0;
/*      */ 
/* 1191 */     char[] arrayOfChar = { this.endSegmentChar };
/*      */ 
/* 1194 */     getClass(); LogMsg(null, "parseSegment :             >> Begin Parsing", 1);
/*      */ 
/* 1197 */     int k = 0;
/* 1198 */     String str1 = this.currSegment.substring(0, 4);
/*      */     try
/*      */     {
/* 1203 */       if (this.isFirstSegment == true)
/*      */       {
/* 1205 */         this.isFirstSegment = false;
/*      */ 
/* 1207 */         getClass(); LogMsg(null, "parseSegment :             >> Parsing Segment <" + str1.toUpperCase() + ">", 1);
/* 1208 */         if (str1.compareTo("DRC|") == 0)
/*      */         {
/* 1210 */           getClass(); int j = this.currSegment.indexOf("|", k + 4);
/* 1211 */           if (j >= 0)
/*      */           {
/* 1213 */             String str2 = this.currSegment.substring(k + 4, j);
/* 1214 */             getClass(); LogMsg(null, "parseSegment :             \t>> Found DRC Directive, parsed SOA Command <" + str2 + ">", 1);
/* 1215 */             if ((str2.compareTo("REG-TEAM") == 0) || (str2.compareTo("UNREG-TEAM") == 0) || (str2.compareTo("QUERY-TEAM") == 0) || (str2.compareTo("PUB-SERVICE") == 0) || (str2.compareTo("QUERY-SERVICE") == 0) || (str2.compareTo("EXEC-SERVICE") == 0))
/*      */             {
/* 1217 */               if (str2.compareTo("REG-TEAM") == 0)
/*      */               {
/* 1220 */                 if (this.currSegment.compareTo("DRC|REG-TEAM|||") == 0)
/*      */                 {
/* 1222 */                   this.whichCommand = 1;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1226 */                   getClass(); LogMsg(null, "parseSegment :                 >> DRC|REG-TEAM segment not according to Spec.", 1);
/* 1227 */                   this.errorMessage = "DRC/REG-TEAM segment not according to Spec.";
/* 1228 */                   this.errorCode = -2;
/*      */                 }
/*      */               }
/* 1231 */               if (str2.compareTo("UNREG-TEAM") == 0)
/*      */               {
/* 1234 */                 this.teamName = "";
/* 1235 */                 this.teamID = "";
/*      */ 
/* 1238 */                 k = j + 1;
/* 1239 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1240 */                 if (j >= 0) this.teamName = this.currSegment.substring(k, j);
/*      */ 
/* 1242 */                 k = j + 1;
/* 1243 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1244 */                 if (j >= 0) this.teamID = this.currSegment.substring(k, j);
/*      */ 
/* 1246 */                 if ((this.teamName.length() > 0) && (this.teamID.length() > 0))
/*      */                 {
/* 1248 */                   this.whichCommand = 2;
/* 1249 */                   i = ProcessUnregTeam();
/*      */                 }
/*      */                 else
/*      */                 {
/* 1253 */                   getClass(); LogMsg(null, "parseSegment :                 >> DRC|UNREG-TEAM segment not according to Spec.", 1);
/* 1254 */                   this.errorMessage = "DRC/UNREG-TEAM segment not according to Spec.";
/* 1255 */                   this.errorCode = -2;
/*      */                 }
/*      */               }
/* 1258 */               if (str2.compareTo("QUERY-TEAM") == 0)
/*      */               {
/* 1261 */                 this.teamName = "";
/* 1262 */                 this.teamID = "";
/*      */ 
/* 1265 */                 k = j + 1;
/* 1266 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1267 */                 if (j >= 0) this.teamName = this.currSegment.substring(k, j);
/*      */ 
/* 1269 */                 k = j + 1;
/* 1270 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1271 */                 if (j >= 0) this.teamID = this.currSegment.substring(k, j);
/*      */ 
/* 1273 */                 if ((this.teamName.length() > 0) && (this.teamID.length() > 0))
/*      */                 {
/* 1275 */                   this.whichCommand = 3;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1279 */                   getClass(); LogMsg(null, "parseSegment :                 >> DRC|QUERY-TEAM segment not according to Spec.", 1);
/* 1280 */                   this.errorMessage = "DRC/QUERY-TEAM segment not according to Spec.";
/* 1281 */                   this.errorCode = -2;
/*      */                 }
/*      */               }
/* 1284 */               if (str2.compareTo("QUERY-SERVICE") == 0)
/*      */               {
/* 1287 */                 this.teamName = "";
/* 1288 */                 this.teamID = "";
/*      */ 
/* 1291 */                 k = j + 1;
/* 1292 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1293 */                 if (j >= 0) this.teamName = this.currSegment.substring(k, j);
/*      */ 
/* 1295 */                 k = j + 1;
/* 1296 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1297 */                 if (j >= 0) this.teamID = this.currSegment.substring(k, j);
/*      */ 
/* 1299 */                 if ((this.teamName.length() > 0) && (this.teamID.length() > 0))
/*      */                 {
/* 1301 */                   this.whichCommand = 5;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1305 */                   getClass(); LogMsg(null, "parseSegment :                 >> DRC|QUERY-SERVICE segment not according to Spec.", 1);
/* 1306 */                   this.errorMessage = "DRC/QUERY-SERVICE segment not according to Spec.";
/* 1307 */                   this.errorCode = -2;
/*      */                 }
/*      */               }
/* 1310 */               if (str2.compareTo("PUB-SERVICE") == 0)
/*      */               {
/* 1313 */                 this.teamName = "";
/* 1314 */                 this.teamID = "";
/*      */ 
/* 1317 */                 k = j + 1;
/* 1318 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1319 */                 if (j >= 0) this.teamName = this.currSegment.substring(k, j);
/*      */ 
/* 1321 */                 k = j + 1;
/* 1322 */                 getClass(); j = this.currSegment.indexOf("|", k);
/* 1323 */                 if (j >= 0) this.teamID = this.currSegment.substring(k, j);
/*      */ 
/* 1325 */                 if ((this.teamName.length() > 0) && (this.teamID.length() > 0))
/*      */                 {
/* 1327 */                   this.whichCommand = 4;
/* 1328 */                   this.spooledMessage = "";
/* 1329 */                   this.numSpooledMessages = 0;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1333 */                   getClass(); LogMsg(null, "parseSegment :                 >> DRC|PUB-SERVICE segment not according to Spec.", 1);
/* 1334 */                   this.errorMessage = "DRC/PUB-SERVICE segment not according to Spec.";
/* 1335 */                   this.errorCode = -2;
/*      */                 }
/*      */               }
/* 1338 */               if (str2.compareTo("EXEC-SERVICE") == 0)
/*      */               {
/* 1340 */                 getClass(); LogMsg(null, "parseSegment :                 >> EXEC-SERVICE command not processed by SOA-Registry.", 1);
/* 1341 */                 this.errorMessage = "EXEC-SERVICE command not processed by SOA-Registry.";
/* 1342 */                 this.errorCode = -2;
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 1347 */               getClass(); LogMsg(null, "parseSegment :                 >> SOA command <" + str2.toUpperCase() + "> - UNKNOWN", 1);
/* 1348 */               this.errorMessage = ("SOA command <" + str2.toUpperCase() + "> - UNKNOWN");
/* 1349 */               this.errorCode = -1;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1354 */             getClass(); LogMsg(null, "parseSegment :                 >> DRC directive has no embedded SOA command", 1);
/* 1355 */             this.errorMessage = "DRC directive has no embedded SOA command";
/* 1356 */             this.errorCode = -1;
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1362 */           getClass(); LogMsg(null, "parseSegment :                 >> Failed to find DRC directive in first segment", 1);
/* 1363 */           this.errorMessage = "DRC directive not in first message segment";
/* 1364 */           this.errorCode = -1;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1369 */         if (this.whichCommand == 1)
/*      */         {
/* 1371 */           i = ProcessRegTeam();
/*      */         }
/* 1373 */         if (this.whichCommand == 3)
/*      */         {
/* 1375 */           i = ProcessQueryTeam();
/*      */         }
/* 1377 */         if (this.whichCommand == 5)
/*      */         {
/* 1379 */           i = ProcessQueryService();
/*      */         }
/* 1381 */         if (this.whichCommand == 4)
/*      */         {
/* 1385 */           this.numSpooledMessages += 1;
/* 1386 */           this.spooledMessage = (this.spooledMessage + this.currSegment + new String(arrayOfChar));
/* 1387 */           if (str1.compareTo("MCH|") == 0) i = ProcessPubService();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/* 1393 */       getClass(); LogMsg(localException, "parseSegment :             ** EXCEPTION **", 1);
/*      */     }
/*      */ 
/* 1396 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private int ProcessRegTeam()
/*      */   {
/* 1406 */     int i = 0;
/*      */ 
/* 1410 */     int k = 0;
/* 1411 */     String str1 = this.currSegment.substring(0, 4);
/* 1412 */     getClass(); LogMsg(null, "processRegTeam :             >> Parsing Segment <" + str1.toUpperCase() + ">", 1);
/*      */ 
/* 1415 */     if (str1.compareTo("INF|") == 0)
/*      */     {
/* 1417 */       getClass(); int j = this.currSegment.indexOf("|", k + 4);
/* 1418 */       if (j >= 0)
/*      */       {
/* 1420 */         String str2 = this.currSegment.substring(k + 4, j);
/*      */ 
/* 1422 */         if (this.currSegment.substring(j).compareTo("|||") == 0)
/*      */         {
/* 1424 */           if (str2.length() > 0)
/*      */           {
/* 1426 */             int m = 0;
/*      */ 
/* 1430 */             String str3 = "";
/*      */             PreparedStatement localPreparedStatement1;
/*      */             ResultSet localResultSet;
/*      */             try {
/* 1435 */               str3 = "select teamID, expirationTime from Team where teamName='" + str2 + "';";
/* 1436 */               localPreparedStatement1 = getDBase().prepareStatement(str3);
/* 1437 */               localPreparedStatement1.execute();
/* 1438 */               localResultSet = localPreparedStatement1.getResultSet();
/* 1439 */               if (!localResultSet.next())
/*      */               {
/* 1441 */                 getClass(); LogMsg(null, "processRegTeam :                >> No team found with name <" + str2 + ">", 0);
/*      */               }
/*      */               else
/*      */               {
/* 1445 */                 getClass(); LogMsg(null, "processRegTeam :                >> Team <" + str2 + "> already registered", 0);
/* 1446 */                 this.teamID = localResultSet.getString(1);
/* 1447 */                 this.teamExpire = localResultSet.getString(2);
/* 1448 */                 m = 1;
/*      */               }
/* 1450 */               localPreparedStatement1.close();
/*      */             }
/*      */             catch (SQLException localSQLException1)
/*      */             {
/* 1454 */               getClass(); LogMsg(null, "processRegTeam :                >> Error executing SQL=[" + str3 + "] - error=[" + localSQLException1.getMessage() + "]", 1);
/* 1455 */               this.errorMessage = ("Error executing SQL=[" + str3 + "] - error=[" + localSQLException1.getMessage() + "]");
/* 1456 */               this.errorCode = -5;
/* 1457 */               m = 1;
/*      */             }
/*      */ 
/* 1460 */             if (m == 0)
/*      */             {
/* 1466 */               getClass(); getClass(); getClass(); int n = 1 + (int)(Math.random() * (3 - 1 + 1));
/*      */ 
/* 1469 */               String str5 = "";
/* 1470 */               String str4 = "";
/* 1471 */               if (this.timeout >= 0)
/*      */               {
/* 1473 */                 str5 = getDateAfterXMinutes(this.timeout);
/* 1474 */                 str4 = getTimeAfterXMinutes(this.timeout);
/*      */               }
/*      */ 
/*      */               try
/*      */               {
/* 1479 */                 str3 = "insert into Team (teamName, securityLevel, expirationDate, expirationTime) VALUES('" + str2 + "'," + n + ",'" + str5 + "','" + str4 + "');";
/* 1480 */                 PreparedStatement localPreparedStatement2 = getDBase().prepareStatement(str3);
/* 1481 */                 localPreparedStatement2.execute();
/* 1482 */                 getClass(); LogMsg(null, "processRegTeam :                >> Inserted team <" + str2 + "> into DBase", 0);
/* 1483 */                 localPreparedStatement2.close();
/*      */ 
/* 1485 */                 str3 = "select teamID, expirationTime from Team where teamName='" + str2 + "';";
/* 1486 */                 localPreparedStatement1 = getDBase().prepareStatement(str3);
/* 1487 */                 localPreparedStatement1.execute();
/* 1488 */                 localResultSet = localPreparedStatement1.getResultSet();
/* 1489 */                 if (!localResultSet.next())
/*      */                 {
/* 1491 */                   getClass(); LogMsg(null, "processRegTeam :                >> No team found with name <" + str2 + "> - Insert FAILED", 0);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1495 */                   getClass(); LogMsg(null, "processRegTeam :                >> Team <" + str2 + "> registered successfully", 0);
/* 1496 */                   this.teamID = localResultSet.getString(1);
/* 1497 */                   this.teamExpire = localResultSet.getString(2);
/* 1498 */                   m = 1;
/*      */                 }
/* 1500 */                 localPreparedStatement1.close();
/*      */               }
/*      */               catch (SQLException localSQLException2)
/*      */               {
/* 1504 */                 getClass(); LogMsg(null, "processRegTeam :                >> Error executing SQL=[" + str3 + "] - error=[" + localSQLException2.getMessage() + "]", 1);
/* 1505 */                 this.errorMessage = ("Error executing SQL=[" + str3 + "] - error=[" + localSQLException2.getMessage() + "]");
/* 1506 */                 this.errorCode = -5;
/* 1507 */                 m = 1;
/*      */               }
/*      */             }
/* 1510 */             if (m != 0)
/*      */             {
/* 1512 */               this.okContent = (this.teamID + "|" + this.teamExpire + "||");
/* 1513 */               this.okBody = "";
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1520 */             getClass(); LogMsg(null, "processRegTeam :                 >> Illegal teamName in INF segment.", 1);
/* 1521 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 1523 */               this.errorMessage = "Illegal teamName in INF segment.";
/* 1524 */               this.errorCode = -3;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1531 */           getClass(); LogMsg(null, "processRegTeam :                 >> INF segment not according to Spec.", 1);
/* 1532 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 1534 */             this.errorMessage = "INF segment not according to Spec.";
/* 1535 */             this.errorCode = -2;
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1541 */         getClass(); LogMsg(null, "processRegTeam :                 >> INF segment not according to Spec.", 1);
/* 1542 */         if (this.errorMessage.length() == 0)
/*      */         {
/* 1544 */           this.errorMessage = "INF segment not according to Spec.";
/* 1545 */           this.errorCode = -2;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1552 */       getClass(); LogMsg(null, "processRegTeam :                 >> Failed to find INF directive in second segment", 1);
/* 1553 */       if (this.errorMessage.length() == 0)
/*      */       {
/* 1555 */         this.errorMessage = "INF directive not in second message segment";
/* 1556 */         this.errorCode = -1;
/*      */       }
/*      */     }
/*      */ 
/* 1560 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private int ProcessUnregTeam()
/*      */   {
/* 1571 */     int i = 0;
/* 1572 */     int j = 0;
/*      */ 
/* 1576 */     String str = "";
/*      */ 
/* 1578 */     getClass(); LogMsg(null, "processUnRegTeam :             >> Attempting to UNREGISTER team " + this.teamName + " (ID : " + this.teamID + ")", 1);
/*      */     try
/*      */     {
/* 1583 */       str = "select teamID, expirationTime from Team where teamName='" + this.teamName + "' and teamID=" + this.teamID + ";";
/* 1584 */       PreparedStatement localPreparedStatement1 = getDBase().prepareStatement(str);
/* 1585 */       localPreparedStatement1.execute();
/* 1586 */       ResultSet localResultSet = localPreparedStatement1.getResultSet();
/* 1587 */       if (!localResultSet.next())
/*      */       {
/* 1589 */         getClass(); LogMsg(null, "processUnRegTeam :               >> No team '" + this.teamName + "' (ID : " + this.teamID + ") found registered in DBase", 1);
/* 1590 */         this.errorMessage = ("No team '" + this.teamName + "' (ID : " + this.teamID + ") found registered in DBase");
/* 1591 */         this.errorCode = -4;
/* 1592 */         i = 1;
/*      */       }
/* 1594 */       localPreparedStatement1.close();
/*      */ 
/* 1596 */       if (i == 0)
/*      */       {
/* 1599 */         str = "delete from Response where serviceID in (select serviceID from Service where teamID=" + this.teamID + ");";
/* 1600 */         PreparedStatement localPreparedStatement2 = getDBase().prepareStatement(str);
/* 1601 */         localPreparedStatement2.execute();
/* 1602 */         int k = localPreparedStatement2.getUpdateCount();
/* 1603 */         getClass(); LogMsg(null, "processUnRegTeam :                  >> Removed " + k + " row(s) from the Response table belonging to team '" + this.teamName + "' (ID : " + this.teamID + ")", 0);
/* 1604 */         localPreparedStatement2.close();
/*      */ 
/* 1606 */         str = "delete from Argument where serviceID in (select serviceID from Service where teamID=" + this.teamID + ");";
/* 1607 */         localPreparedStatement2 = getDBase().prepareStatement(str);
/* 1608 */         localPreparedStatement2.execute();
/* 1609 */         k = localPreparedStatement2.getUpdateCount();
/* 1610 */         getClass(); LogMsg(null, "processUnRegTeam :                  >> Removed " + k + " row(s) from the Argument table belonging to team '" + this.teamName + "' (ID : " + this.teamID + ")", 0);
/* 1611 */         localPreparedStatement2.close();
/*      */ 
/* 1613 */         str = "delete from Service where teamID=" + this.teamID + ";";
/* 1614 */         localPreparedStatement2 = getDBase().prepareStatement(str);
/* 1615 */         localPreparedStatement2.execute();
/* 1616 */         k = localPreparedStatement2.getUpdateCount();
/* 1617 */         getClass(); LogMsg(null, "processUnRegTeam :                  >> Removed " + k + " row(s) from the Service table belonging to team '" + this.teamName + "' (ID : " + this.teamID + ")", 0);
/* 1618 */         localPreparedStatement2.close();
/*      */ 
/* 1620 */         str = "delete from Team where teamID=" + this.teamID + ";";
/* 1621 */         localPreparedStatement2 = getDBase().prepareStatement(str);
/* 1622 */         localPreparedStatement2.execute();
/* 1623 */         k = localPreparedStatement2.getUpdateCount();
/* 1624 */         getClass(); LogMsg(null, "processUnRegTeam :                  >> Removed " + k + " row(s) from the Team table belonging to team '" + this.teamName + "' (ID : " + this.teamID + ")", 0);
/* 1625 */         localPreparedStatement2.close();
/*      */       }
/*      */     }
/*      */     catch (SQLException localSQLException)
/*      */     {
/* 1630 */       getClass(); LogMsg(null, "processUnRegTeam :                >> Error executing SQL=[" + str + "] - error=[" + localSQLException.getMessage() + "]", 1);
/* 1631 */       this.errorMessage = ("Error executing SQL=[" + str + "] - error=[" + localSQLException.getMessage() + "]");
/* 1632 */       this.errorCode = -5;
/* 1633 */       i = 1;
/*      */     }
/*      */ 
/* 1638 */     if (this.errorCode == 0)
/*      */     {
/* 1640 */       this.okContent = "|||";
/* 1641 */       this.okBody = "";
/*      */     }
/*      */ 
/* 1644 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private int ProcessQueryTeam()
/*      */   {
/* 1654 */     int i = 0;
/*      */ 
/* 1658 */     int k = 0;
/* 1659 */     String str1 = this.currSegment.substring(0, 4);
/* 1660 */     getClass(); LogMsg(null, "processQueryTeam :             >> Parsing Segment <" + str1.toUpperCase() + ">", 1);
/*      */ 
/* 1663 */     if (str1.compareTo("INF|") == 0)
/*      */     {
/* 1666 */       String str2 = "";
/* 1667 */       getClass(); int j = this.currSegment.indexOf("|", k + 4);
/* 1668 */       if (j >= 0) str2 = this.currSegment.substring(k + 4, j);
/*      */ 
/* 1671 */       String str3 = "";
/* 1672 */       k = j + 1;
/* 1673 */       getClass(); j = this.currSegment.indexOf("|", k);
/* 1674 */       if (j >= 0) str3 = this.currSegment.substring(k, j);
/*      */ 
/* 1677 */       String str4 = "";
/* 1678 */       k = j + 1;
/* 1679 */       getClass(); j = this.currSegment.indexOf("|", k);
/* 1680 */       if (j >= 0) str4 = this.currSegment.substring(k, j);
/*      */ 
/* 1683 */       if ((str2.length() > 0) && (str3.length() > 0) && (str4.length() > 0)) {
/* 1685 */         int m = 0;
/*      */ 
/* 1689 */         String str5 = "";
/* 1690 */         String str6 = "";
/*      */         PreparedStatement localPreparedStatement;
/*      */         ResultSet localResultSet;
/*      */         try {
/* 1695 */           str5 = "select t.teamID, s.securityLevel from Team t, Service s where t.teamName='" + this.teamName + "' and t.teamID=" + this.teamID + " and s.teamID=t.teamID and s.tagName='" + str4 + "';";
/* 1696 */           localPreparedStatement = getDBase().prepareStatement(str5);
/* 1697 */           localPreparedStatement.execute();
/* 1698 */           localResultSet = localPreparedStatement.getResultSet();
/* 1699 */           if (!localResultSet.next())
/*      */           {
/* 1701 */             getClass(); LogMsg(null, "processQueryTeam :               >> Team '" + this.teamName + "' (ID : " + this.teamID + ") does not have service " + str4 + " registered in DBase", 1);
/* 1702 */             this.errorMessage = ("Team '" + this.teamName + "' (ID : " + this.teamID + ") does not have service " + str4 + " registered in DBase");
/* 1703 */             this.errorCode = -4;
/* 1704 */             m = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1708 */             str6 = localResultSet.getString(2);
/* 1709 */             if ((str6 != null) && (str6.length() > 0))
/*      */             {
/* 1711 */               getClass(); LogMsg(null, "processQueryTeam :               >> Found service <" + str4 + "> for team '" + this.teamName + "' (secLevel=" + str6 + ")", 1);
/*      */             }
/*      */             else
/*      */             {
/* 1715 */               getClass(); LogMsg(null, "processQueryTeam :               >> No service <" + str4 + "> for team '" + this.teamName + "' found in DBase", 1);
/* 1716 */               this.errorMessage = ("No service <" + str4 + "> for team '" + this.teamName + "' found in DBase");
/* 1717 */               this.errorCode = -4;
/* 1718 */               m = 1;
/*      */             }
/*      */           }
/* 1721 */           localPreparedStatement.close();
/*      */         }
/*      */         catch (SQLException localSQLException1)
/*      */         {
/* 1725 */           getClass(); LogMsg(null, "processQueryTeam :                >> Error executing SQL=[" + str5 + "] - error=[" + localSQLException1.getMessage() + "]", 1);
/* 1726 */           this.errorMessage = ("Error executing SQL=[" + str5 + "] - error=[" + localSQLException1.getMessage() + "]");
/* 1727 */           this.errorCode = -5;
/* 1728 */           m = 1;
/*      */         }
/*      */ 
/* 1731 */         if (m == 0)
/*      */         {
/*      */           try
/*      */           {
/* 1735 */             str5 = "select teamID, securityLevel from Team where teamName='" + str2 + "' and teamID=" + str3 + " and securityLevel>=" + str6 + ";";
/* 1736 */             localPreparedStatement = getDBase().prepareStatement(str5);
/* 1737 */             localPreparedStatement.execute();
/* 1738 */             localResultSet = localPreparedStatement.getResultSet();
/* 1739 */             if (!localResultSet.next())
/*      */             {
/* 1741 */               getClass(); LogMsg(null, "processQueryTeam :               >> Team <" + str2 + "> does not have adequate security level to run service " + str4, 1);
/* 1742 */               this.errorMessage = ("Team <" + str2 + "> does not have adequate security level to run service " + str4);
/* 1743 */               this.errorCode = -4;
/* 1744 */               m = 1;
/*      */             }
/* 1746 */             localPreparedStatement.close();
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/* 1750 */             getClass(); LogMsg(null, "processQueryTeam :                >> Error executing SQL=[" + str5 + "] - error=[" + localSQLException2.getMessage() + "]", 1);
/* 1751 */             this.errorMessage = ("Error executing SQL=[" + str5 + "] - error=[" + localSQLException2.getMessage() + "]");
/* 1752 */             this.errorCode = -5;
/* 1753 */             m = 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1759 */         getClass(); LogMsg(null, "processQueryTeam :                 >> INF segment not according to Spec.", 1);
/* 1760 */         if (str2.length() == 0)
/*      */         {
/* 1762 */           getClass(); LogMsg(null, "processQueryTeam :                   >> Calling teamName is blank.", 1);
/* 1763 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 1765 */             this.errorMessage = "INF segment not according to Spec (calling teamName is BLANK).";
/* 1766 */             this.errorCode = -2;
/*      */           }
/*      */         }
/* 1769 */         if (str3.length() == 0)
/*      */         {
/* 1771 */           getClass(); LogMsg(null, "processQueryTeam :                   >> Calling teamID is blank.", 1);
/* 1772 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 1774 */             this.errorMessage = "INF segment not according to Spec (calling teamID is BLANK).";
/* 1775 */             this.errorCode = -2;
/*      */           }
/*      */         }
/* 1778 */         if (str4.length() == 0)
/*      */         {
/* 1780 */           getClass(); LogMsg(null, "processQueryTeam :                   >> Requested tagName is blank.", 1);
/* 1781 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 1783 */             this.errorMessage = "INF segment not according to Spec (requested tagName is BLANK).";
/* 1784 */             this.errorCode = -2;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1792 */       getClass(); LogMsg(null, "processQueryTeam :                 >> Failed to find INF directive in second segment", 1);
/* 1793 */       if (this.errorMessage.length() == 0)
/*      */       {
/* 1795 */         this.errorMessage = "INF directive not in second message segment";
/* 1796 */         this.errorCode = -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1802 */     if (this.errorCode == 0)
/*      */     {
/* 1804 */       this.okContent = "|||";
/* 1805 */       this.okBody = "";
/*      */     }
/* 1807 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private int ProcessPubService()
/*      */   {
/* 1817 */     int i = 0;
/*      */ 
/* 1821 */     int i3 = -1;
/* 1822 */     int i4 = -1;
/*      */ 
/* 1824 */     String str1 = "";
/*      */ 
/* 1828 */     int i5 = 0;
/* 1829 */     int i6 = 0;
/* 1830 */     int i7 = 1;
/* 1831 */     int i8 = 1;
/* 1832 */     getClass(); LogMsg(null, "processPubService :             >> Parsing " + this.numSpooledMessages + " Segment(s)", 1);
/*      */ 
/* 1834 */     int n = 0;
/* 1835 */     int i1 = -1;
/* 1836 */     for (int m = 0; m < this.numSpooledMessages; m++)
/*      */     {
/* 1839 */       if (this.errorCode != 0)
/*      */       {
/*      */         break;
/*      */       }
/* 1843 */       n = i1 + 1;
/* 1844 */       i1 = this.spooledMessage.indexOf(this.endSegmentChar, n);
/*      */ 
/* 1846 */       this.currSegment = this.spooledMessage.substring(n, i1);
/*      */ 
/* 1849 */       int i2 = 0;
/* 1850 */       int k = 0;
/* 1851 */       String str2 = this.currSegment.substring(k, 4);
/* 1852 */       getClass(); LogMsg(null, "processPubService :             >> Parsing Segment <" + str2.toUpperCase() + ">", 1);
/*      */       String str3;
/*      */       int j;
/*      */       String str4;
/*      */       String str5;
/*      */       String str6;
/*      */       int i9;
/*      */       String str9;
/*      */       PreparedStatement localPreparedStatement1;
/*      */       Object localObject;
/*      */       PreparedStatement localPreparedStatement2;
/* 1855 */       if (str2.compareTo("SRV|") == 0)
/*      */       {
/* 1858 */         str3 = "";
/* 1859 */         getClass(); j = this.currSegment.indexOf("|", k + 4);
/* 1860 */         if (j >= 0) str3 = this.currSegment.substring(k + 4, j);
/* 1861 */         if (str3.length() > 0) i2++;
/*      */ 
/* 1864 */         str4 = "";
/* 1865 */         k = j + 1;
/* 1866 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 1867 */         if (j >= 0) str4 = this.currSegment.substring(k, j);
/* 1868 */         if (str4.length() > 0) i2++;
/*      */ 
/* 1871 */         str5 = "";
/* 1872 */         k = j + 1;
/* 1873 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 1874 */         if (j >= 0) str5 = this.currSegment.substring(k, j);
/* 1875 */         if (str5.length() > 0) i2++;
/*      */ 
/* 1878 */         str6 = "";
/* 1879 */         k = j + 1;
/* 1880 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 1881 */         if (j >= 0) str6 = this.currSegment.substring(k, j);
/* 1882 */         if (str6.length() > 0) i2++;
/*      */ 
/* 1885 */         String str7 = "";
/* 1886 */         k = j + 1;
/* 1887 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 1888 */         if (j >= 0) str7 = this.currSegment.substring(k, j);
/* 1889 */         if (str7.length() > 0) i2++;
/*      */ 
/* 1892 */         String str8 = "";
/* 1893 */         k = j + 1;
/* 1894 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 1895 */         if ((j >= 0) && (k != j)) str8 = this.currSegment.substring(k, j);
/*      */ 
/* 1897 */         if (i2 >= 5)
/*      */         {
/* 1899 */           i9 = 0;
/*      */ 
/* 1903 */           str9 = "";
/* 1904 */           String str10 = "";
/*      */ 
/* 1907 */           if (!isValidTagName(str3))
/*      */           {
/* 1909 */             getClass(); LogMsg(null, "processPubService :               >> Tagname (" + str3 + ") is not valid", 1);
/* 1910 */             this.errorMessage = ("Tagname (" + str3 + ") is not valid");
/* 1911 */             this.errorCode = -2;
/* 1912 */             i9 = 1;
/*      */           }
/* 1914 */           if ((i9 == 0) && (!isAlphaNumeric(str4)))
/*      */           {
/* 1916 */             getClass(); LogMsg(null, "processPubService :               >> ServiceName (" + str4 + ") contains invalid characters", 1);
/* 1917 */             this.errorMessage = ("ServiceName (" + str4 + ") contains invalid characters");
/* 1918 */             this.errorCode = -2;
/* 1919 */             i9 = 1;
/*      */           }
/* 1921 */           if ((i9 == 0) && (!isSecLevelValid(str5)))
/*      */           {
/* 1923 */             getClass(); LogMsg(null, "processPubService :               >> Security Level (" + str5 + ") contains invalid value", 1);
/* 1924 */             this.errorMessage = ("Security Level (" + str5 + ") contains invalid value");
/* 1925 */             this.errorCode = -2;
/* 1926 */             i9 = 1;
/*      */           }
/* 1928 */           if ((i9 == 0) && (!inValidRange(str6, 0, -1)))
/*      */           {
/* 1930 */             getClass(); LogMsg(null, "processPubService :               >> Number of Arguments (" + str6 + ") must be greater than or equal to zero", 1);
/* 1931 */             this.errorMessage = ("Number of Arguments (" + str6 + ") must be greater than or equal to zero");
/* 1932 */             this.errorCode = -2;
/* 1933 */             i9 = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1937 */             i3 = Integer.parseInt(str6);
/*      */           }
/*      */ 
/* 1940 */           if ((i9 == 0) && (!inValidRange(str7, 1, -1)))
/*      */           {
/* 1942 */             getClass(); LogMsg(null, "processPubService :               >> Number of Responses (" + str7 + ") must be greater than or equal to one", 1);
/* 1943 */             this.errorMessage = ("Number of Responses (" + str7 + ") must be greater than or equal to one");
/* 1944 */             this.errorCode = -2;
/* 1945 */             i9 = 1;
/*      */           }
/*      */           else
/*      */           {
/* 1949 */             i4 = Integer.parseInt(str7);
/*      */           }
/*      */ 
/* 1952 */           if ((i9 == 0) && (str8.length() > 0) && (!isValidCommentCharacters(str8)))
/*      */           {
/* 1954 */             getClass(); LogMsg(null, "processPubService :               >> Service Description contains invalid characters", 1);
/* 1955 */             this.errorMessage = "Service Description contains invalid characters";
/* 1956 */             this.errorCode = -2;
/* 1957 */             i9 = 1;
/*      */           }
/* 1959 */           if ((i9 == 0) && (str8.length() > 0) && (str8.length() > 200))
/*      */           {
/* 1961 */             getClass(); LogMsg(null, "processPubService :               >> Service Description too long (needs to be less than 200 characters", 1);
/* 1962 */             this.errorMessage = "Service Description too long (needs to be less than 200 characters";
/* 1963 */             this.errorCode = -2;
/* 1964 */             i9 = 1;
/*      */           }
/*      */ 
/* 1967 */           if (i9 == 0)
/*      */           {
/*      */             try
/*      */             {
/* 1972 */               str9 = "select teamID from Team where teamID=" + this.teamID + " and teamName='" + this.teamName + "';";
/* 1973 */               localPreparedStatement1 = getDBase().prepareStatement(str9);
/* 1974 */               localPreparedStatement1.execute();
/* 1975 */               localObject = localPreparedStatement1.getResultSet();
/* 1976 */               if (!((ResultSet)localObject).next())
/*      */               {
/* 1978 */                 getClass(); LogMsg(null, "processPubService :               >> Team '" + this.teamName + "' (ID : " + this.teamID + ") is not registered", 1);
/* 1979 */                 this.errorMessage = ("Team '" + this.teamName + "' (ID : " + this.teamID + ") is not registered");
/* 1980 */                 this.errorCode = -4;
/* 1981 */                 i9 = 1;
/*      */               }
/* 1983 */               localPreparedStatement1.close();
/*      */             }
/*      */             catch (SQLException localSQLException4)
/*      */             {
/* 1987 */               getClass(); LogMsg(null, "processPubService :                >> Error executing SQL=[" + str9 + "] - error=[" + localSQLException4.getMessage() + "]", 1);
/* 1988 */               this.errorMessage = ("Error executing SQL=[" + str9 + "] - error=[" + localSQLException4.getMessage() + "]");
/* 1989 */               this.errorCode = -5;
/* 1990 */               i9 = 1;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1995 */           if (i9 == 0)
/*      */           {
/*      */             try
/*      */             {
/* 2000 */               str9 = "select serviceID from Service where teamID=" + this.teamID + " and tagName='" + str3 + "';";
/* 2001 */               localPreparedStatement1 = getDBase().prepareStatement(str9);
/* 2002 */               localPreparedStatement1.execute();
/* 2003 */               localObject = localPreparedStatement1.getResultSet();
/* 2004 */               if (((ResultSet)localObject).next())
/*      */               {
/* 2006 */                 getClass(); LogMsg(null, "processPubService :               >> Team '" + this.teamName + "' (ID : " + this.teamID + ") has already published service " + str3, 1);
/* 2007 */                 this.errorMessage = ("Team '" + this.teamName + "' (ID : " + this.teamID + ") has already published service " + str3);
/* 2008 */                 this.errorCode = -4;
/* 2009 */                 i9 = 1;
/*      */               }
/*      */               else
/*      */               {
/* 2014 */                 str9 = "insert into Service(teamID, tagName, serviceName, securityLevel, description, ipAddress, port) VALUES(" + this.teamID + ",'" + str3 + "','" + str4 + "'," + str5 + ",'" + str8 + "','','');";
/* 2015 */                 localPreparedStatement2 = getDBase().prepareStatement(str9);
/* 2016 */                 localPreparedStatement2.execute();
/*      */ 
/* 2018 */                 getClass(); LogMsg(null, "processPubService :                >> Inserted service <" + str3 + "> into DBase for teamID " + this.teamID, 0);
/* 2019 */                 localPreparedStatement2.close();
/*      */ 
/* 2021 */                 str9 = "select serviceID from Service where teamID=" + this.teamID + " and tagName='" + str3 + "';";
/* 2022 */                 localPreparedStatement1 = getDBase().prepareStatement(str9);
/* 2023 */                 localPreparedStatement1.execute();
/* 2024 */                 localObject = localPreparedStatement1.getResultSet();
/* 2025 */                 if (!((ResultSet)localObject).next())
/*      */                 {
/* 2027 */                   getClass(); LogMsg(null, "processPubService :                >> No service found with name <" + str3 + "> for teamID " + this.teamID + " - Insert FAILED", 0);
/*      */                 }
/*      */                 else
/*      */                 {
/* 2031 */                   getClass(); LogMsg(null, "processPubService :                >> Service <" + str3 + "> registered successfully", 0);
/* 2032 */                   str1 = ((ResultSet)localObject).getString(1);
/*      */                 }
/*      */               }
/* 2035 */               localPreparedStatement1.close();
/*      */             }
/*      */             catch (SQLException localSQLException5)
/*      */             {
/* 2039 */               getClass(); LogMsg(null, "processPubService :                >> Error executing SQL=[" + str9 + "] - error=[" + localSQLException5.getMessage() + "]", 1);
/* 2040 */               this.errorMessage = ("Error executing SQL=[" + str9 + "] - error=[" + localSQLException5.getMessage() + "]");
/* 2041 */               this.errorCode = -5;
/* 2042 */               i9 = 1;
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 2048 */           getClass(); LogMsg(null, "processPubService :                 >> SRV segment not according to Spec.", 1);
/* 2049 */           if (str3.length() == 0)
/*      */           {
/* 2051 */             getClass(); LogMsg(null, "processPubService :                   >> Service tagName is blank.", 1);
/* 2052 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2054 */               this.errorMessage = "SRV segment not according to Spec (service tagName is BLANK).";
/* 2055 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2058 */           if (str4.length() == 0)
/*      */           {
/* 2060 */             getClass(); LogMsg(null, "processPubService :                   >> Service name is blank.", 1);
/* 2061 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2063 */               this.errorMessage = "SRV segment not according to Spec (service name is BLANK).";
/* 2064 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2067 */           if (str5.length() == 0)
/*      */           {
/* 2069 */             getClass(); LogMsg(null, "processPubService :                   >> Security Level is blank.", 1);
/* 2070 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2072 */               this.errorMessage = "SRV segment not according to Spec (security level is BLANK).";
/* 2073 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2076 */           if (str6.length() == 0)
/*      */           {
/* 2078 */             getClass(); LogMsg(null, "processPubService :                   >> Number of Arguments is blank.", 1);
/* 2079 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2081 */               this.errorMessage = "SRV segment not according to Spec (numArgs is BLANK).";
/* 2082 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2085 */           if (str7.length() == 0)
/*      */           {
/* 2087 */             getClass(); LogMsg(null, "processPubService :                   >> Number of Service Responses is blank.", 1);
/* 2088 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2090 */               this.errorMessage = "SRV segment not according to Spec (numResps is BLANK).";
/* 2091 */               this.errorCode = -2;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/* 2098 */       else if (str2.compareTo("ARG|") == 0)
/*      */       {
/* 2100 */         i5++;
/*      */ 
/* 2103 */         str3 = "";
/* 2104 */         getClass(); j = this.currSegment.indexOf("|", k + 4);
/* 2105 */         if (j >= 0) str3 = this.currSegment.substring(k + 4, j);
/* 2106 */         if (str3.length() > 0) i2++;
/*      */ 
/* 2109 */         str4 = "";
/* 2110 */         k = j + 1;
/* 2111 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2112 */         if (j >= 0) str4 = this.currSegment.substring(k, j);
/* 2113 */         if (str4.length() > 0) i2++;
/*      */ 
/* 2116 */         str5 = "";
/* 2117 */         k = j + 1;
/* 2118 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2119 */         if (j >= 0) str5 = this.currSegment.substring(k, j);
/* 2120 */         if (str5.length() > 0) i2++;
/*      */ 
/* 2123 */         str6 = "";
/* 2124 */         k = j + 1;
/* 2125 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2126 */         if (j >= 0) str6 = this.currSegment.substring(k, j);
/* 2127 */         if (str6.length() > 0) i2++;
/*      */ 
/* 2129 */         if (i2 == 4)
/*      */         {
/* 2131 */           i9 = 0;
/*      */ 
/* 2135 */           str9 = "";
/*      */ 
/* 2138 */           if (!inValidRange(str3, i7, i7))
/*      */           {
/* 2140 */             getClass(); LogMsg(null, "processPubService :               >> ArgPosition (" + str3 + ") is not valid", 1);
/* 2141 */             this.errorMessage = ("ARG Segment #" + i5 + " - ArgPosition (" + str3 + ") is not valid - expected " + i7);
/* 2142 */             this.errorCode = -2;
/* 2143 */             i9 = 1;
/*      */           }
/*      */           else
/*      */           {
/* 2147 */             i7 = Integer.parseInt(str3) + 1;
/*      */           }
/*      */ 
/* 2150 */           if ((i9 == 0) && (!isAlphaNumeric(str4)))
/*      */           {
/* 2152 */             getClass(); LogMsg(null, "processPubService :               >> ArgName (" + str4 + ") contains invalid characters", 1);
/* 2153 */             this.errorMessage = ("ARG Segment #" + i5 + " - ArgName (" + str4 + ") contains invalid characters");
/* 2154 */             this.errorCode = -2;
/* 2155 */             i9 = 1;
/*      */           }
/*      */ 
/* 2158 */           if ((i9 == 0) && (!isValidDatatype(str5)))
/*      */           {
/* 2160 */             getClass(); LogMsg(null, "processPubService :               >> ArgDatatype (" + str5 + ") is not valid", 1);
/* 2161 */             this.errorMessage = ("ARG Segment #" + i5 + " - ArgDatatype (" + str5 + ") is not valid");
/* 2162 */             this.errorCode = -2;
/* 2163 */             i9 = 1;
/*      */           }
/*      */ 
/* 2166 */           if ((i9 == 0) && (!isValidArgtype(str6)))
/*      */           {
/* 2168 */             getClass(); LogMsg(null, "processPubService :               >> ArgMandatoryOptional (" + str6 + ") is not valid", 1);
/* 2169 */             this.errorMessage = ("ARG Segment #" + i5 + " - ArgMandatoryOptional (" + str6 + ") is not valid");
/* 2170 */             this.errorCode = -2;
/* 2171 */             i9 = 1;
/*      */           }
/*      */           else
/*      */           {
/* 2176 */             if (str6.compareToIgnoreCase("mandatory") == 0) str6 = "NO";
/* 2177 */             if (str6.compareToIgnoreCase("optional") == 0) str6 = "YES";
/*      */           }
/* 2179 */           if (i9 == 0)
/*      */           {
/*      */             try
/*      */             {
/* 2184 */               str9 = "insert into Argument(serviceID, argName, argDatatype, argPosition, argOptional) VALUES(" + str1 + ",'" + str4 + "','" + str5.toUpperCase() + "'," + str3 + ",'" + str6 + "');";
/* 2185 */               localPreparedStatement2 = getDBase().prepareStatement(str9);
/* 2186 */               localPreparedStatement2.execute();
/*      */ 
/* 2188 */               getClass(); LogMsg(null, "processPubService :                >> Inserted argument #" + i5 + " into DBase for serviceID " + str1, 0);
/* 2189 */               localPreparedStatement2.close();
/*      */             }
/*      */             catch (SQLException localSQLException2)
/*      */             {
/* 2193 */               getClass(); LogMsg(null, "processPubService :                >> Error executing SQL=[" + str9 + "] - error=[" + localSQLException2.getMessage() + "]", 1);
/* 2194 */               this.errorMessage = ("Error executing SQL=[" + str9 + "] - error=[" + localSQLException2.getMessage() + "]");
/* 2195 */               this.errorCode = -5;
/* 2196 */               i9 = 1;
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 2202 */           getClass(); LogMsg(null, "processPubService :                 >> ARG segment (#" + i5 + ") not according to Spec.", 1);
/* 2203 */           if (str3.length() == 0)
/*      */           {
/* 2205 */             getClass(); LogMsg(null, "processPubService :                   >> Argument Position is blank.", 1);
/* 2206 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2208 */               this.errorMessage = ("ARG segment (#" + i5 + ") not according to Spec (argPosition is BLANK).");
/* 2209 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2212 */           if (str4.length() == 0)
/*      */           {
/* 2214 */             getClass(); LogMsg(null, "processPubService :                   >> Argument Name is blank.", 1);
/* 2215 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2217 */               this.errorMessage = ("ARG segment (#" + i5 + ") not according to Spec (argName is BLANK).");
/* 2218 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2221 */           if (str5.length() == 0)
/*      */           {
/* 2223 */             getClass(); LogMsg(null, "processPubService :                   >> Argument DataType is blank.", 1);
/* 2224 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2226 */               this.errorMessage = ("ARG segment (#" + i5 + ") not according to Spec (argDatatype is BLANK).");
/* 2227 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2230 */           if (str6.length() == 0)
/*      */           {
/* 2232 */             getClass(); LogMsg(null, "processPubService :                   >> Argument 'mandatoriness' is blank.", 1);
/* 2233 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2235 */               this.errorMessage = ("ARG segment (#" + i5 + ") not according to Spec (is the argument mandatory or optional).");
/* 2236 */               this.errorCode = -2;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 2242 */       else if (str2.compareTo("RSP|") == 0)
/*      */       {
/* 2244 */         i6++;
/*      */ 
/* 2247 */         str3 = "";
/* 2248 */         getClass(); j = this.currSegment.indexOf("|", k + 4);
/* 2249 */         if (j >= 0) str3 = this.currSegment.substring(k + 4, j);
/* 2250 */         if (str3.length() > 0) i2++;
/*      */ 
/* 2253 */         str4 = "";
/* 2254 */         k = j + 1;
/* 2255 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2256 */         if (j >= 0) str4 = this.currSegment.substring(k, j);
/* 2257 */         if (str4.length() > 0) i2++;
/*      */ 
/* 2260 */         str5 = "";
/* 2261 */         k = j + 1;
/* 2262 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2263 */         if (j >= 0) str5 = this.currSegment.substring(k, j);
/* 2264 */         if (str5.length() > 0) i2++;
/*      */ 
/* 2267 */         str6 = "";
/* 2268 */         k = j + 1;
/* 2269 */         getClass(); j = this.currSegment.indexOf("|", k);
/* 2270 */         if (j >= 0) str6 = this.currSegment.substring(k, j);
/* 2271 */         if (str6.length() == 0) i2++;
/*      */ 
/* 2273 */         if (i2 == 4)
/*      */         {
/* 2275 */           i9 = 0;
/*      */ 
/* 2279 */           str9 = "";
/*      */ 
/* 2282 */           if (!inValidRange(str3, i8, i8))
/*      */           {
/* 2284 */             getClass(); LogMsg(null, "processPubService :               >> RespPosition (" + str3 + ") is not valid", 1);
/* 2285 */             this.errorMessage = ("RSP Segment #" + i6 + " - RespPosition (" + str3 + ") is not valid - expected " + i8);
/* 2286 */             this.errorCode = -2;
/* 2287 */             i9 = 1;
/*      */           }
/*      */           else
/*      */           {
/* 2291 */             i8 = Integer.parseInt(str3) + 1;
/*      */           }
/*      */ 
/* 2294 */           if ((i9 == 0) && (!isAlphaNumeric(str4)))
/*      */           {
/* 2296 */             getClass(); LogMsg(null, "processPubService :               >> RespName (" + str4 + ") contains invalid characters", 1);
/* 2297 */             this.errorMessage = ("RSP Segment #" + i6 + " - RespName (" + str4 + ") contains invalid characters");
/* 2298 */             this.errorCode = -2;
/* 2299 */             i9 = 1;
/*      */           }
/*      */ 
/* 2302 */           if ((i9 == 0) && (!isValidDatatype(str5)))
/*      */           {
/* 2304 */             getClass(); LogMsg(null, "processPubService :               >> RespDatatype (" + str5 + ") is not valid", 1);
/* 2305 */             this.errorMessage = ("RSP Segment #" + i6 + " - RespDatatype (" + str5 + ") is not valid");
/* 2306 */             this.errorCode = -2;
/* 2307 */             i9 = 1;
/*      */           }
/*      */ 
/* 2310 */           if (i9 == 0)
/*      */           {
/*      */             try
/*      */             {
/* 2315 */               str9 = "insert into Response(serviceID, rspName, rspDatatype, rspPosition) VALUES(" + str1 + ",'" + str4 + "','" + str5.toUpperCase() + "'," + str3 + ");";
/* 2316 */               localPreparedStatement2 = getDBase().prepareStatement(str9);
/* 2317 */               localPreparedStatement2.execute();
/*      */ 
/* 2319 */               getClass(); LogMsg(null, "processPubService :                >> Inserted response #" + i6 + " into DBase for serviceID " + str1, 0);
/* 2320 */               localPreparedStatement2.close();
/*      */             }
/*      */             catch (SQLException localSQLException3)
/*      */             {
/* 2324 */               getClass(); LogMsg(null, "processPubService :                >> Error executing SQL=[" + str9 + "] - error=[" + localSQLException3.getMessage() + "]", 1);
/* 2325 */               this.errorMessage = ("Error executing SQL=[" + str9 + "] - error=[" + localSQLException3.getMessage() + "]");
/* 2326 */               this.errorCode = -5;
/* 2327 */               i9 = 1;
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 2333 */           getClass(); LogMsg(null, "processPubService :                 >> RSP segment (#" + i6 + ") not according to Spec.", 1);
/* 2334 */           if (str3.length() == 0)
/*      */           {
/* 2336 */             getClass(); LogMsg(null, "processPubService :                   >> Response Position is blank.", 1);
/* 2337 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2339 */               this.errorMessage = ("RSP segment (#" + i6 + ") not according to Spec (rspPosition is BLANK).");
/* 2340 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2343 */           if (str4.length() == 0)
/*      */           {
/* 2345 */             getClass(); LogMsg(null, "processPubService :                   >> Response Name is blank.", 1);
/* 2346 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2348 */               this.errorMessage = ("RSP segment (#" + i6 + ") not according to Spec (rspName is BLANK).");
/* 2349 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2352 */           if (str5.length() == 0)
/*      */           {
/* 2354 */             getClass(); LogMsg(null, "processPubService :                   >> Response DataType is blank.", 1);
/* 2355 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2357 */               this.errorMessage = ("RSP segment (#" + i6 + ") not according to Spec (rspDatatype is BLANK).");
/* 2358 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2361 */           if (str6.length() > 0)
/*      */           {
/* 2363 */             getClass(); LogMsg(null, "processPubService :                   >> Last response field must be blank.", 1);
/* 2364 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2366 */               this.errorMessage = ("RSP segment (#" + i6 + ") not according to Spec (last response field must be blank).");
/* 2367 */               this.errorCode = -2;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/* 2373 */       else if (str2.compareTo("MCH|") == 0)
/*      */       {
/* 2376 */         if (((i3 != i5) && (i3 > 0)) || ((i4 != i6) && (i4 > 0)))
/*      */         {
/* 2378 */           getClass(); LogMsg(null, "processPubService :                 >> ARG/RSP segment(s) not consistent with SRV segment.", 1);
/* 2379 */           if (i3 != i5)
/*      */           {
/* 2381 */             getClass(); LogMsg(null, "processPubService :                   >> SRV said " + i3 + " ARG segments - found " + i5 + " such segments.", 1);
/* 2382 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2384 */               this.errorMessage = ("Inconsistent ARG segments - SRV said " + i3 + " ARG segments - found " + i5 + " such segments..");
/* 2385 */               this.errorCode = -2;
/*      */             }
/*      */           }
/* 2388 */           if (i4 != i6)
/*      */           {
/* 2390 */             getClass(); LogMsg(null, "processPubService :                   >> SRV said " + i4 + " RSP segments - found " + i6 + " such segments.", 1);
/* 2391 */             if (this.errorMessage.length() == 0)
/*      */             {
/* 2393 */               this.errorMessage = ("Inconsistent RSP segments - SRV said " + i4 + " RSP segments - found " + i6 + " such segments..");
/* 2394 */               this.errorCode = -2;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2401 */           str3 = "";
/* 2402 */           getClass(); j = this.currSegment.indexOf("|", k + 4);
/* 2403 */           if (j >= 0) str3 = this.currSegment.substring(k + 4, j);
/* 2404 */           if (str3.length() > 0) i2++;
/*      */ 
/* 2407 */           str4 = "";
/* 2408 */           k = j + 1;
/* 2409 */           getClass(); j = this.currSegment.indexOf("|", k);
/* 2410 */           if (j >= 0) str4 = this.currSegment.substring(k, j);
/* 2411 */           if (str4.length() > 0) i2++;
/*      */ 
/* 2413 */           if (i2 == 2)
/*      */           {
/* 2415 */             i9 = 0;
/*      */ 
/* 2419 */             localObject = "";
/*      */ 
/* 2422 */             if (!isValidIPFormat(str3))
/*      */             {
/* 2424 */               getClass(); LogMsg(null, "processPubService :               >> Service IP Address (" + str3 + ") is not valid format", 1);
/* 2425 */               this.errorMessage = ("MCH segment - Service IP Address (" + str3 + ")is not valid format.");
/* 2426 */               this.errorCode = -2;
/* 2427 */               i9 = 1;
/*      */             }
/*      */ 
/* 2430 */             if ((i9 == 0) && (!isValidIPAddress(str3)))
/*      */             {
/* 2432 */               if (this.errOnUnreachIP == true)
/*      */               {
/* 2434 */                 getClass(); LogMsg(null, "processPubService :               >> Service IP Address (" + str3 + ") is not reachable", 1);
/* 2435 */                 this.errorMessage = ("MCH segment - Service IP Address (" + str3 + ") is not reachable.");
/* 2436 */                 this.errorCode = -2;
/* 2437 */                 i9 = 1;
/*      */               }
/*      */               else
/*      */               {
/* 2441 */                 getClass(); LogMsg(null, "processPubService :               >> Service IP Address (" + str3 + ") is not reachable", 1);
/*      */               }
/*      */             }
/*      */ 
/* 2445 */             if ((i9 == 0) && (!inValidRange(str4, 2000, -1)))
/*      */             {
/* 2447 */               getClass(); LogMsg(null, "processPubService :               >> Service Port (" + str4 + ") not in valid range", 1);
/* 2448 */               this.errorMessage = ("MCH Segment - Service Port (" + str4 + ") not in valid range");
/* 2449 */               this.errorCode = -2;
/* 2450 */               i9 = 1;
/*      */             }
/*      */ 
/* 2453 */             if ((i9 == 0) && (!isValidPublishLocation(str3, str4)))
/*      */             {
/* 2455 */               if (this.errOnUnconnectedPort == true)
/*      */               {
/* 2457 */                 getClass(); LogMsg(null, "processPubService :               >> Publish Location (" + str3 + ", port " + str4 + ") is not accepting connections", 1);
/* 2458 */                 this.errorMessage = ("MCH segment - Publish Location (" + str3 + ", port " + str4 + ") is not accepting connections.");
/* 2459 */                 this.errorCode = -2;
/* 2460 */                 i9 = 1;
/*      */               }
/*      */               else
/*      */               {
/* 2464 */                 getClass(); LogMsg(null, "processPubService :               >> Publish Location (" + str3 + ", port " + str4 + ") is not accepting connections", 1);
/*      */               }
/*      */             }
/*      */ 
/* 2468 */             if (i9 == 0)
/*      */             {
/*      */               try
/*      */               {
/* 2473 */                 localObject = "update Service set ipaddress='" + str3 + "', port='" + str4 + "' where serviceID=" + str1 + ";";
/* 2474 */                 localPreparedStatement1 = getDBase().prepareStatement((String)localObject);
/* 2475 */                 localPreparedStatement1.execute();
/* 2476 */                 int i10 = localPreparedStatement1.getUpdateCount();
/*      */ 
/* 2478 */                 if (i10 == 1)
/*      */                 {
/* 2480 */                   getClass(); LogMsg(null, "processPubService :                >> Updated service in DBase for serviceID " + str1, 0);
/*      */                 }
/*      */                 else
/*      */                 {
/* 2484 */                   getClass(); LogMsg(null, "processPubService :                >> Failed to update service in DBase for serviceID " + str1, 0);
/*      */                 }
/* 2486 */                 localPreparedStatement1.close();
/*      */               }
/*      */               catch (SQLException localSQLException1)
/*      */               {
/* 2490 */                 getClass(); LogMsg(null, "processPubService :                >> Error executing SQL=[" + (String)localObject + "] - error=[" + localSQLException1.getMessage() + "]", 1);
/* 2491 */                 this.errorMessage = ("Error executing SQL=[" + (String)localObject + "] - error=[" + localSQLException1.getMessage() + "]");
/* 2492 */                 this.errorCode = -5;
/* 2493 */                 i9 = 1;
/*      */               }
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 2499 */             getClass(); LogMsg(null, "processPubService :                 >> MCH segment not according to Spec.", 1);
/* 2500 */             if (str3.length() == 0)
/*      */             {
/* 2502 */               getClass(); LogMsg(null, "processPubService :                   >> Server's IP Address is blank.", 1);
/* 2503 */               if (this.errorMessage.length() == 0)
/*      */               {
/* 2505 */                 this.errorMessage = "MSG segment not according to Spec (serverIPAddr is BLANK).";
/* 2506 */                 this.errorCode = -2;
/*      */               }
/*      */             }
/* 2509 */             if (str4.length() == 0)
/*      */             {
/* 2511 */               getClass(); LogMsg(null, "processPubService :                   >> Server's Listening Port is blank.", 1);
/* 2512 */               if (this.errorMessage.length() == 0)
/*      */               {
/* 2514 */                 this.errorMessage = "MCH segment not according to Spec (serverPort is BLANK).";
/* 2515 */                 this.errorCode = -2;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2524 */         getClass(); LogMsg(null, "processPubService :                 >> Invalid segment directive found (" + str2 + ")", 1);
/* 2525 */         if (this.errorMessage.length() == 0)
/*      */         {
/* 2527 */           this.errorMessage = ("Invalid segment directive found (" + str2 + ")");
/* 2528 */           this.errorCode = -1;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2535 */     if (this.errorCode == 0)
/*      */     {
/* 2537 */       this.okContent = "|||";
/* 2538 */       this.okBody = "";
/*      */     }
/* 2540 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private int ProcessQueryService()
/*      */   {
/* 2550 */     int i = 0;
/* 2551 */     int j = 0;
/* 2552 */     int k = 1;
/* 2553 */     int m = 0;
/* 2554 */     int n = 0;
/* 2555 */     int i1 = 0;
/*      */ 
/* 2562 */     String str4 = "";
/* 2563 */     String str5 = "";
/* 2564 */     String str6 = "";
/* 2565 */     String str7 = "";
/* 2566 */     String str8 = "";
/* 2567 */     String str9 = "";
/* 2568 */     String str10 = "optional";
/* 2569 */     char[] arrayOfChar = new char[1];
/* 2570 */     arrayOfChar[0] = this.endSegmentChar;
/*      */ 
/* 2572 */     int i3 = 0;
/* 2573 */     String str1 = this.currSegment.substring(0, 4);
/* 2574 */     getClass(); LogMsg(null, "processQueryService :             >> Parsing Segment <" + str1.toUpperCase() + ">", 1);
/*      */ 
/* 2577 */     if (str1.compareTo("SRV|") == 0)
/*      */     {
/* 2580 */       String str2 = "";
/* 2581 */       getClass(); int i2 = this.currSegment.indexOf("|", i3 + 4);
/* 2582 */       if (i2 >= 0) str2 = this.currSegment.substring(i3 + 4, i2);
/*      */ 
/* 2585 */       i3 = i2 + 1;
/* 2586 */       String str3 = this.currSegment.substring(i3);
/*      */ 
/* 2588 */       if ((str2.length() > 0) && (str3.compareTo("|||||") == 0)) {
/* 2590 */         int i4 = 0;
/*      */ 
/* 2593 */         String str11 = "";
/*      */         PreparedStatement localPreparedStatement;
/*      */         ResultSet localResultSet;
/*      */         try {
/* 2598 */           str11 = "select teamID from Team where teamName='" + this.teamName + "' and teamID=" + this.teamID + ";";
/* 2599 */           localPreparedStatement = getDBase().prepareStatement(str11);
/* 2600 */           localPreparedStatement.execute();
/* 2601 */           localResultSet = localPreparedStatement.getResultSet();
/* 2602 */           if (!localResultSet.next())
/*      */           {
/* 2604 */             getClass(); LogMsg(null, "processQueryService :               >> Team '" + this.teamName + "' (ID : " + this.teamID + ") is not registered in DBase", 1);
/* 2605 */             this.errorMessage = ("Team '" + this.teamName + "' (ID : " + this.teamID + ") is not registered in DBase");
/* 2606 */             this.errorCode = -4;
/* 2607 */             i4 = 1;
/*      */           }
/* 2609 */           localPreparedStatement.close();
/*      */         }
/*      */         catch (SQLException localSQLException1)
/*      */         {
/* 2613 */           getClass(); LogMsg(null, "processQueryService :                >> Error executing SQL=[" + str11 + "] - error=[" + localSQLException1.getMessage() + "]", 1);
/* 2614 */           this.errorMessage = ("Error executing SQL=[" + str11 + "] - error=[" + localSQLException1.getMessage() + "]");
/* 2615 */           this.errorCode = -5;
/* 2616 */           i4 = 1;
/*      */         }
/*      */ 
/* 2619 */         if (i4 == 0)
/*      */         {
/* 2621 */           if (!isValidTagName(str2))
/*      */           {
/* 2623 */             getClass(); LogMsg(null, "processQueryService :               >> Tagname (" + str2 + ") is not valid", 1);
/* 2624 */             this.errorMessage = ("Tagname (" + str2 + ") is not valid");
/* 2625 */             this.errorCode = -2;
/* 2626 */             i4 = 1;
/*      */           }
/*      */         }
/*      */ 
/* 2630 */         if (i4 == 0)
/*      */         {
/*      */           try
/*      */           {
/* 2634 */             str11 = "select count(*) from Service where tagName='" + str2 + "';";
/* 2635 */             if (!this.teamTestOwn) str11 = "select count(*) from Service where tagName='" + str2 + "' and teamID!=" + this.teamID + ";";
/* 2636 */             localPreparedStatement = getDBase().prepareStatement(str11);
/* 2637 */             localPreparedStatement.execute();
/* 2638 */             localResultSet = localPreparedStatement.getResultSet();
/* 2639 */             if (!localResultSet.next())
/*      */             {
/* 2641 */               getClass(); LogMsg(null, "processQueryService :               >> No <" + str2 + "> services exist in the DBase", 1);
/* 2642 */               this.errorMessage = ("No <" + str2 + "> services exist in the DBase");
/* 2643 */               this.errorCode = -4;
/* 2644 */               i4 = 1;
/*      */             }
/*      */             else
/*      */             {
/* 2648 */               j = Integer.parseInt(localResultSet.getString(1));
/* 2649 */               if (j == 0)
/*      */               {
/* 2651 */                 getClass(); LogMsg(null, "processQueryService :               >> No <" + str2 + "> services exist in the DBase", 1);
/* 2652 */                 this.errorMessage = ("No <" + str2 + "> services exist in the DBase");
/* 2653 */                 this.errorCode = -4;
/* 2654 */                 i4 = 1;
/*      */               }
/*      */               else
/*      */               {
/* 2658 */                 k = 1 + (int)(Math.random() * (j - 1 + 1));
/*      */               }
/*      */             }
/* 2661 */             localPreparedStatement.close();
/*      */           }
/*      */           catch (SQLException localSQLException2)
/*      */           {
/* 2665 */             getClass(); LogMsg(null, "processQueryService :                >> Error executing SQL=[" + str11 + "] - error=[" + localSQLException2.getMessage() + "]", 1);
/* 2666 */             this.errorMessage = ("Error executing SQL=[" + str11 + "] - error=[" + localSQLException2.getMessage() + "]");
/* 2667 */             this.errorCode = -5;
/* 2668 */             i4 = 1;
/*      */           }
/*      */         }
/*      */ 
/* 2672 */         if (i4 == 0)
/*      */         {
/*      */           try
/*      */           {
/* 2676 */             str11 = "select serviceID, teamID, description, ipAddress, port, serviceName from Service where tagName='" + str2 + "';";
/* 2677 */             if (!this.teamTestOwn) str11 = "select  serviceID, teamID, description, ipAddress, port, serviceName from Service where tagName='" + str2 + "' and teamID!=" + this.teamID + ";";
/* 2678 */             localPreparedStatement = getDBase().prepareStatement(str11);
/* 2679 */             localPreparedStatement.execute();
/* 2680 */             localResultSet = localPreparedStatement.getResultSet();
/* 2681 */             if (!localResultSet.next())
/*      */             {
/* 2683 */               String str12 = "";
/*      */ 
/* 2685 */               if (k == 1) str12 = "1st";
/* 2686 */               if (k == 2) str12 = "2nd";
/* 2687 */               if (k == 3) str12 = "3rd";
/* 2688 */               if (k > 3) str12 = k + "th";
/*      */ 
/* 2690 */               getClass(); LogMsg(null, "processQueryService :               >> An error occurred while selecting the " + str12 + " <" + str2 + "> service in the DBase", 1);
/* 2691 */               this.errorMessage = ("An error occurred while selecting the " + str12 + " <" + str2 + "> service in the DBase");
/* 2692 */               this.errorCode = -4;
/* 2693 */               i4 = 1;
/*      */             }
/*      */ 
/* 2696 */             if (i4 == 0)
/*      */             {
/* 2699 */               if (k > 1)
/*      */               {
/* 2701 */                 for (int i5 = 1; i5 < k; i5++) localResultSet.next();
/*      */ 
/*      */               }
/*      */ 
/* 2705 */               str4 = localResultSet.getString(1);
/* 2706 */               str5 = localResultSet.getString(2);
/* 2707 */               str6 = localResultSet.getString(3);
/* 2708 */               str7 = localResultSet.getString(4);
/* 2709 */               str8 = localResultSet.getString(5);
/* 2710 */               str9 = localResultSet.getString(6);
/*      */ 
/* 2713 */               localPreparedStatement.close();
/*      */ 
/* 2715 */               str11 = "select count(*) from Argument where serviceID=" + str4 + ";";
/* 2716 */               localPreparedStatement = getDBase().prepareStatement(str11);
/* 2717 */               localPreparedStatement.execute();
/* 2718 */               localResultSet = localPreparedStatement.getResultSet();
/* 2719 */               if (!localResultSet.next())
/*      */               {
/* 2721 */                 getClass(); LogMsg(null, "processQueryService :               >> An error occurred while retrieving the number of arguments for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase", 1);
/* 2722 */                 this.errorMessage = ("An error occurred while retrieving the number of arguments for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase");
/* 2723 */                 this.errorCode = -4;
/* 2724 */                 i4 = 1;
/*      */               }
/*      */               else
/*      */               {
/* 2728 */                 m = Integer.parseInt(localResultSet.getString(1));
/* 2729 */                 localPreparedStatement.close();
/*      */ 
/* 2731 */                 str11 = "select count(*) from Response where serviceID=" + str4 + ";";
/* 2732 */                 localPreparedStatement = getDBase().prepareStatement(str11);
/* 2733 */                 localPreparedStatement.execute();
/* 2734 */                 localResultSet = localPreparedStatement.getResultSet();
/* 2735 */                 if (!localResultSet.next())
/*      */                 {
/* 2737 */                   getClass(); LogMsg(null, "processQueryService :               >> An error occurred while retrieving the number of responses for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase", 1);
/* 2738 */                   this.errorMessage = ("An error occurred while retrieving the number of responses for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase");
/* 2739 */                   this.errorCode = -4;
/* 2740 */                   i4 = 1;
/*      */                 }
/*      */                 else
/*      */                 {
/* 2744 */                   n = Integer.parseInt(localResultSet.getString(1));
/* 2745 */                   i1 = 2 + m + n;
/*      */                 }
/*      */               }
/*      */             }
/* 2749 */             localPreparedStatement.close();
/*      */           }
/*      */           catch (SQLException localSQLException3)
/*      */           {
/* 2753 */             getClass(); LogMsg(null, "processQueryService :                >> Error executing SQL=[" + str11 + "] - error=[" + localSQLException3.getMessage() + "]", 1);
/* 2754 */             this.errorMessage = ("Error executing SQL=[" + str11 + "] - error=[" + localSQLException3.getMessage() + "]");
/* 2755 */             this.errorCode = -5;
/* 2756 */             i4 = 1;
/*      */           }
/*      */         }
/*      */ 
/* 2760 */         if (i4 == 0)
/*      */         {
/* 2764 */           this.okBody = ("SRV||" + str9 + "||" + m + "|" + n + "|" + str6 + "|" + new String(arrayOfChar));
/*      */           try
/*      */           {
/* 2769 */             str11 = "select argPosition, argName, argDatatype, argOptional from Argument where serviceID='" + str4 + "' order by argPosition;";
/* 2770 */             localPreparedStatement = getDBase().prepareStatement(str11);
/* 2771 */             localPreparedStatement.execute();
/* 2772 */             localResultSet = localPreparedStatement.getResultSet();
/* 2773 */             if (!localResultSet.next())
/*      */             {
/* 2775 */               getClass(); LogMsg(null, "processQueryService :               >> An error occurred while retrieving the arguments for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase", 1);
/* 2776 */               this.errorMessage = ("An error occurred while retrieving the arguments for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase");
/* 2777 */               this.errorCode = -4;
/* 2778 */               i4 = 1;
/*      */             }
/*      */             else
/*      */             {
/*      */               do
/*      */               {
/* 2784 */                 str10 = "optional";
/* 2785 */                 if (localResultSet.getString(4).compareTo("NO") == 0) str10 = "mandatory";
/* 2786 */                 this.okBody = (this.okBody + "ARG|" + localResultSet.getString(1) + "|" + localResultSet.getString(2) + "|" + localResultSet.getString(3) + "|" + str10 + "||" + new String(arrayOfChar));
/* 2787 */               }while (localResultSet.next());
/*      */             }
/*      */ 
/* 2790 */             if (i4 == 0)
/*      */             {
/* 2792 */               localPreparedStatement.close();
/*      */ 
/* 2795 */               str11 = "select rspPosition, rspName, rspDatatype from Response where serviceID='" + str4 + "' order by rspPosition;";
/* 2796 */               localPreparedStatement = getDBase().prepareStatement(str11);
/* 2797 */               localPreparedStatement.execute();
/* 2798 */               localResultSet = localPreparedStatement.getResultSet();
/* 2799 */               if (!localResultSet.next())
/*      */               {
/* 2801 */                 getClass(); LogMsg(null, "processQueryService :               >> An error occurred while retrieving the responses for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase", 1);
/* 2802 */                 this.errorMessage = ("An error occurred while retrieving the responses for <" + str2 + "> service (serviceID=" + str4 + ") in the DBase");
/* 2803 */                 this.errorCode = -4;
/* 2804 */                 i4 = 1;
/*      */               }
/*      */               else
/*      */               {
/*      */                 do
/*      */                 {
/* 2810 */                   this.okBody = (this.okBody + "RSP|" + localResultSet.getString(1) + "|" + localResultSet.getString(2) + "|" + localResultSet.getString(3) + "||" + new String(arrayOfChar));
/* 2811 */                 }while (localResultSet.next());
/*      */               }
/*      */             }
/* 2814 */             localPreparedStatement.close();
/*      */ 
/* 2816 */             if (i4 == 0)
/*      */             {
/* 2819 */               this.okBody = (this.okBody + "MCH|" + str7 + "|" + str8 + "|" + new String(arrayOfChar));
/*      */             }
/*      */           }
/*      */           catch (SQLException localSQLException4)
/*      */           {
/* 2824 */             getClass(); LogMsg(null, "processQueryService :                >> Error executing SQL=[" + str11 + "] - error=[" + localSQLException4.getMessage() + "]", 1);
/* 2825 */             this.errorMessage = ("Error executing SQL=[" + str11 + "] - error=[" + localSQLException4.getMessage() + "]");
/* 2826 */             this.errorCode = -5;
/* 2827 */             i4 = 1;
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2833 */         getClass(); LogMsg(null, "processQueryService :                 >> SRV segment not according to Spec.", 1);
/* 2834 */         if (str2.length() == 0)
/*      */         {
/* 2836 */           getClass(); LogMsg(null, "processQueryService :                   >> Calling tagName is blank.", 1);
/* 2837 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 2839 */             this.errorMessage = "SRV segment not according to Spec (calling tagName is BLANK).";
/* 2840 */             this.errorCode = -2;
/*      */           }
/*      */         }
/* 2843 */         if (str3.compareTo("|||||") != 0)
/*      */         {
/* 2845 */           getClass(); LogMsg(null, "processQueryService :                   >> All fields after <tagName> must be blank.", 1);
/* 2846 */           if (this.errorMessage.length() == 0)
/*      */           {
/* 2848 */             this.errorMessage = "SRV segment not according to Spec (All fields after <tagName> must be BLANK).";
/* 2849 */             this.errorCode = -2;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 2857 */       getClass(); LogMsg(null, "processQueryService :                 >> Failed to find SRV directive in second segment", 1);
/* 2858 */       if (this.errorMessage.length() == 0)
/*      */       {
/* 2860 */         this.errorMessage = "SRV directive not in second message segment";
/* 2861 */         this.errorCode = -1;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2867 */     if (this.errorCode == 0)
/*      */     {
/* 2869 */       this.okContent = ("||" + i1 + "|");
/*      */     }
/* 2871 */     return this.errorCode;
/*      */   }
/*      */ 
/*      */   private void LogMsg(Exception paramException, String paramString, int paramInt)
/*      */   {
/* 2879 */     BufferedWriter localBufferedWriter = null;
/* 2880 */     String str = this.soaListenerProperties.getProperty("SOAListenerLogLevel", "INFO");
/*      */ 
/* 2884 */     getClass(); int i = 1;
/* 2885 */     if (str.compareTo("DEBUG") == 0) { getClass(); i = 0;
/*      */     }
/* 2887 */     if (paramInt >= i)
/*      */     {
/* 2889 */       File localFile = new File(this.soaListenerProperties.getProperty("SOAListenerLogDir") + "\\SOARegisterListener.log");
/*      */       try
/*      */       {
/* 2892 */         localBufferedWriter = new BufferedWriter(new FileWriter(localFile, true));
/*      */ 
/* 2895 */         if (paramException != null)
/* 2896 */           localBufferedWriter.write(this.FORMAT_TIMESTAMP.format(new Date(System.currentTimeMillis())) + " " + paramString + " " + paramException);
/*      */         else
/* 2898 */           localBufferedWriter.write(this.FORMAT_TIMESTAMP.format(new Date(System.currentTimeMillis())) + " " + paramString);
/* 2899 */         localBufferedWriter.newLine();
/* 2900 */         localBufferedWriter.flush();
/*      */       }
/*      */       catch (IOException localIOException2)
/*      */       {
/* 2904 */         System.out.println("(SOARegisterListener) : Error while logging: " + localIOException2);
/*      */       }
/*      */       finally
/*      */       {
/* 2909 */         if (localBufferedWriter != null)
/*      */         {
/*      */           try
/*      */           {
/* 2913 */             localBufferedWriter.close();
/*      */           }
/*      */           catch (IOException localIOException4)
/*      */           {
/* 2917 */             System.out.println("(SOARegisterListener) : Error while closing logging file : " + localIOException4);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\hekar\Documents\GitHub\soa_assignment_1\SOA\Runtime\SOA-Registry\bin\lib\
 * Qualified Name:     SOASocketListener
 * JD-Core Version:    0.6.2
 */