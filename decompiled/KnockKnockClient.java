/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class KnockKnockClient
/*     */ {
/*     */   private static final int START_MSG = 11;
/*     */   private static final int END_MSG = 28;
/*     */   private static final int END_SEGMENT = 13;
/*     */ 
/*     */   public static void main(String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/*  14 */     Socket localSocket = null;
/*  15 */     PrintWriter localPrintWriter = null;
/*  16 */     BufferedReader localBufferedReader1 = null;
/*     */ 
/*  21 */     String str1 = JOptionPane.showInputDialog(null, "Host to Talk to ?", "Specify the HOST", 1);
/*     */ 
/*  25 */     String str2 = JOptionPane.showInputDialog(null, "What Port is it Listening on ?", "Specify the PORT", 1);
/*     */     int i;
/*     */     try
/*     */     {
/*  31 */       i = Integer.parseInt(str2);
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/*  35 */       i = 3128;
/*     */     }
/*     */ 
/*  46 */     char[] arrayOfChar1 = { '\013' };
/*  47 */     char[] arrayOfChar2 = { '\034' };
/*  48 */     char[] arrayOfChar3 = { '\r' };
/*     */ 
/*  50 */     int k = 1;
/*  51 */     int m = 0;
/*  52 */     int n = 0;
/*     */ 
/*  55 */     String str3 = "";
/*  56 */     String str5 = JOptionPane.showInputDialog(null, "Filename Containing HL7 Message", "Test Filename", 1);
/*     */     try
/*     */     {
/*  66 */       localSocket = new Socket(str1, i);
/*  67 */       localPrintWriter = new PrintWriter(localSocket.getOutputStream(), true);
/*  68 */       localBufferedReader1 = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/*     */     }
/*     */     catch (UnknownHostException localUnknownHostException)
/*     */     {
/*  72 */       System.out.println("Don't know about host: " + str1);
/*  73 */       System.exit(1);
/*     */     }
/*     */     catch (IOException localIOException)
/*     */     {
/*  77 */       System.out.println("Couldn't get I/O for the Connection to " + str1 + " on Port (" + str2 + ")");
/*  78 */       System.exit(1);
/*     */     }
/*     */ 
/*  81 */     while (str5.length() > 0)
/*     */     {
/*     */       try
/*     */       {
/*  86 */         File localFile = new File(".\\" + str5);
/*  87 */         BufferedReader localBufferedReader2 = new BufferedReader(new FileReader(localFile));
/*     */ 
/*  89 */         long l = localFile.length();
/*  90 */         System.out.println("  >> Transmitting File : .\\" + str5 + " (" + l + " bytes)");
/*     */ 
/*  92 */         String str7 = new String(arrayOfChar1);
/*     */         String str6;
/*  93 */         while ((str6 = localBufferedReader2.readLine()) != null)
/*     */         {
/*  96 */           l = l - str6.length() - 1L;
/*  97 */           System.out.println("     >> Segment read from file (segLength=" + str6.length() + " bytes, " + l + " bytes remaining to send)");
/*     */ 
/*  99 */           if (k != 0)
/*     */           {
/* 101 */             k = 0;
/*     */           }
/*     */ 
/* 105 */           str7 = str7 + str6 + new String(arrayOfChar3);
/*     */ 
/* 108 */           if (l < 6L)
/*     */           {
/* 111 */             if ((str7.indexOf("READY") >= 0) || (str7.indexOf("BYE") >= 0))
/*     */             {
/* 114 */               str7 = str7.substring(0, str7.length() - 1);
/*     */             }
/*     */             else
/*     */             {
/* 118 */               str7 = str7 + new String(arrayOfChar2);
/*     */             }
/* 120 */             m = 1;
/*     */           }
/*     */ 
/* 125 */           if (m != 0) break;
/*     */ 
/*     */         }
/*     */ 
/* 129 */         if (m == 0)
/*     */         {
/* 131 */           str7 = str7 + new String(arrayOfChar2);
/* 132 */           m = 1;
/*     */         }
/*     */ 
/* 135 */         localPrintWriter.println(str7);
/* 136 */         localPrintWriter.flush();
/* 137 */         localBufferedReader2.close();
/* 138 */         System.out.println("  >> Message sent ...");
/*     */ 
/* 141 */         str3 = localBufferedReader1.readLine();
/*     */ 
/* 145 */         if (arrayOfChar3[0] == '\r')
/*     */         {
/* 147 */           n = 1;
/*     */ 
/* 150 */           if (str3.indexOf("SOA|OK|") >= 0)
/*     */           {
/* 153 */             if (str3.indexOf("SOA|OK|||") >= 0)
/*     */             {
/* 155 */               if (str3.indexOf("SOA|OK||||") >= 0) n = 0;
/*     */             }
/*     */             else
/*     */             {
/* 159 */               n = 0;
/*     */             }
/*     */           }
/* 162 */           if (str3.indexOf("SOA|NOT-OK|") >= 0) n = 0;
/*     */ 
/* 164 */           str3 = str3.substring(1) + "\n";
/* 165 */           while (n != 0)
/*     */           {
/* 167 */             String str4 = localBufferedReader1.readLine();
/* 168 */             int j = str4.length() - 1;
/*     */ 
/* 172 */             if (str4.charAt(j) == arrayOfChar2[0])
/*     */             {
/* 175 */               n = 0;
/*     */             }
/*     */             else
/*     */             {
/* 179 */               str3 = str3 + str4 + "\n";
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 184 */         System.out.println("  >> Server Responds : " + str3);
/*     */       }
/*     */       catch (Exception localException2)
/*     */       {
/* 188 */         System.out.println("Exception in Transmitting Data : " + localException2.getMessage());
/* 189 */         localPrintWriter.close();
/* 190 */         localBufferedReader1.close();
/* 191 */         localSocket.close();
/* 192 */         System.exit(1);
/*     */       }
/* 194 */       if (str3.indexOf("AE") >= 0) break;
/* 195 */       str5 = JOptionPane.showInputDialog(null, "Filename Containing HL7 Message", "Test Filename", 1);
/*     */ 
/* 199 */       k = 1;
/* 200 */       m = 0;
/*     */     }
/*     */ 
/* 203 */     localPrintWriter.close();
/* 204 */     localBufferedReader1.close();
/* 205 */     localSocket.close();
/* 206 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           C:\Users\hekar\Documents\GitHub\soa_assignment_1\SOA\Runtime\Sample-Client\bin\lib\
 * Qualified Name:     KnockKnockClient
 * JD-Core Version:    0.6.2
 */