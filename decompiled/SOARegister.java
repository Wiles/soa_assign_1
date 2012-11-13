/*    */ public class SOARegister
/*    */ {
/*    */   public static void main(String[] paramArrayOfString)
/*    */   {
/* 23 */     String str = paramArrayOfString[0];
/*    */ 
/* 27 */     SOASocketListener localSOASocketListener = new SOASocketListener(str);
/* 28 */     localSOASocketListener.start();
/*    */   }
/*    */ }

/* Location:           C:\Users\hekar\Documents\GitHub\soa_assignment_1\SOA\Runtime\SOA-Registry\bin\lib\
 * Qualified Name:     SOARegister
 * JD-Core Version:    0.6.2
 */