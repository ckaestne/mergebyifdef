// 1 "Util1.java.merged"
// 1 "<built-in>"
// 1 "<command-line>"
// 1 "Util1.java.merged"

package hudson;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import hudson.Proc.LocalProc;

import hudson.model.TaskListener;

import hudson.os.PosixAPI;
// 22 "Util1.java.merged"
import hudson.util.IOException2;
import hudson.util.QuotedStringTokenizer;
import hudson.util.VariableResolver;

import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
// 40 "Util1.java.merged"
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;



import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.Copy;

import org.apache.tools.ant.types.FileSet;
// 60 "Util1.java.merged"
import org.jruby.ext.posix.FileStat;
import org.jruby.ext.posix.POSIX;


import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import org.kohsuke.stapler.Stapler;



import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
// 97 "Util1.java.merged"
import java.net.InetAddress;



import java.net.URI;
import java.net.URISyntaxException;

import java.net.UnknownHostException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;

import java.nio.charset.Charset;

import java.nio.charset.CharsetEncoder;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;

import java.util.*;
// 140 "Util1.java.merged"
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hudson.util.jna.GNUCLibrary.LIBC;
// 155 "Util1.java.merged"
public class Util {
    private static final long ONE_SECOND_MS = 1000;
    private static final long ONE_MINUTE_MS = 60 * ONE_SECOND_MS;
    private static final long ONE_HOUR_MS = 60 * ONE_MINUTE_MS;
    private static final long ONE_DAY_MS = 24 * ONE_HOUR_MS;
    private static final long ONE_MONTH_MS = 30 * ONE_DAY_MS;
    private static final long ONE_YEAR_MS = 365 * ONE_DAY_MS;
    public static <T> List<T> filter( Iterable<?> base, Class<T> type ) {
        List<T> r = new ArrayList<T>();
        for (Object i : base) {

            if(type.isInstance(i))




                r.add(type.cast(i));
        }



        return r;
    }
    public static <T> List<T> filter( List<?> base, Class<T> type ) {
        return filter((Iterable)base,type);
    }

    private static final Pattern VARIABLE = Pattern.compile("\\$([A-Za-z0-9_]+|\\{[A-Za-z0-9_]+\\}|\\$)");




    public static String replaceMacro(String s, Map<String,String> properties) {
        return replaceMacro(s,new VariableResolver.ByMap<String>(properties));
    }
    public static String replaceMacro(String s, VariableResolver<String> resolver) {
     if (s == null) {
      return null;
     }
        int idx=0;
        while(true) {
            Matcher m = VARIABLE.matcher(s);

            if(!m.find(idx)) return s;






            String key = m.group().substring(1);
            String value;
            if(key.charAt(0)=='$') {
               value = "$";
            } else {

               if(key.charAt(0)=='{') key = key.substring(1,key.length()-1);






               value = resolver.resolve(key);
            }

            if(value==null)




                idx = m.end();

            else {




                s = s.substring(0,m.start())+value+s.substring(m.end());
                idx = m.start() + value.length();
            }
        }
    }
    public static String loadFile(File logfile) throws IOException {
        return loadFile(logfile, Charset.defaultCharset());
    }
    public static String loadFile(File logfile,Charset charset) throws IOException {

        if(!logfile.exists())




            return "";



        StringBuilder str = new StringBuilder((int)logfile.length());
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(logfile),charset));

        try {

            char[] buf = new char[1024];
            int len;

            while((len=r.read(buf,0,buf.length))>0)




               str.append(buf,0,len);

        } finally {
            r.close();


        }




        return str.toString();
    }
    public static void deleteContentsRecursive(File file) throws IOException {
        File[] files = file.listFiles();

        if(files==null)




            return;

        for (File child : files)





            deleteRecursive(child);
    }



    public static void deleteFile(File f) throws IOException {
        if (!f.delete()) {
            if(!f.exists())



                return;



            makeWritable(f);
            makeWritable(f.getParentFile());
            if(!f.delete() && f.exists()) {
                File[] files = f.listFiles();

                if(files!=null && files.length>0)




                    throw new IOException("Unable to delete " + f.getPath()+" - files in dir: "+Arrays.asList(files));



                throw new IOException("Unable to delete " + f.getPath());
            }
        }
    }

    @IgnoreJRERequirement

    private static void makeWritable(File f) {
        try {
            Chmod chmod = new Chmod();
            chmod.setProject(new Project());
            chmod.setFile(f);
            chmod.setPerm("u+w");
            chmod.execute();
        } catch (BuildException e) {
            LOGGER.log(Level.INFO,"Failed to chmod "+f,e);
        }
        try {
            f.setWritable(true);
        } catch (NoSuchMethodError e) {
        }



        try {

            POSIX posix = PosixAPI.get();
            String path = f.getAbsolutePath();
            FileStat stat = posix.stat(path);
            posix.chmod(path, stat.mode()|0200);
        } catch (Throwable t) {
            LOGGER.log(Level.FINE,"Failed to chmod(2) "+f,t);
// 365 "Util1.java.merged"
        }
    }




    public static void deleteRecursive(File dir) throws IOException {

        if(!isSymlink(dir))
            deleteContentsRecursive(dir);


        try {
            deleteFile(dir);
        } catch (IOException e) {
            if(!isSymlink(dir))





                deleteContentsRecursive(dir);


            deleteFile(dir);


        }





    }




    public static boolean isSymlink(File file) throws IOException {
        String name = file.getName();

        if (name.equals(".") || name.equals(".."))




            return false;



        File fileInCanonicalParent;
        File parentDir = file.getParentFile();
        if ( parentDir == null ) {
            fileInCanonicalParent = file;
        } else {
            fileInCanonicalParent = new File( parentDir.getCanonicalPath(), name );
        }
        return !fileInCanonicalParent.getCanonicalFile().equals( fileInCanonicalParent.getAbsoluteFile() );
    }
    public static File createTempDir() throws IOException {
        File tmp = File.createTempFile("hudson", "tmp");

        if(!tmp.delete())




            throw new IOException("Failed to delete "+tmp);

        if(!tmp.mkdirs())





            throw new IOException("Failed to create a new directory "+tmp);



        return tmp;
    }
    private static final Pattern errorCodeParser = Pattern.compile(".*CreateProcess.*error=([0-9]+).*");
    public static void displayIOException( IOException e, TaskListener listener ) {
        String msg = getWin32ErrorMessage(e);

        if(msg!=null)




            listener.getLogger().println(msg);
    }



    public static String getWin32ErrorMessage(IOException e) {
        return getWin32ErrorMessage((Throwable)e);
    }
    public static String getWin32ErrorMessage(Throwable e) {
        String msg = e.getMessage();
        if(msg!=null) {
            Matcher m = errorCodeParser.matcher(msg);
            if(m.matches()) {
                try {
                    ResourceBundle rb = ResourceBundle.getBundle("/hudson/win32errors");
                    return rb.getString("error"+m.group(1));
                } catch (Exception _) {
                }
            }
        }

        if(e.getCause()!=null)




            return getWin32ErrorMessage(e.getCause());



        return null;
    }
    public static String getWin32ErrorMessage(int n) {

        try {

            ResourceBundle rb = ResourceBundle.getBundle("/hudson/win32errors");
            return rb.getString("error"+n);

        } catch (MissingResourceException e) {
            LOGGER.log(Level.WARNING,"Failed to find resource bundle",e);
            return null;
        }
    }




    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
    public static void copyStream(InputStream in,OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len;

        while((len=in.read(buf))>0)




            out.write(buf,0,len);
    }



    public static void copyStream(Reader in, Writer out) throws IOException {
        char[] buf = new char[8192];
        int len;

        while((len=in.read(buf))>0)




            out.write(buf,0,len);
    }



    public static void copyStreamAndClose(InputStream in,OutputStream out) throws IOException {
        try {
            copyStream(in,out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    public static void copyStreamAndClose(Reader in,Writer out) throws IOException {
        try {
            copyStream(in,out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
    public static String[] tokenize(String s,String delimiter) {
        return QuotedStringTokenizer.tokenize(s,delimiter);
    }
    public static String[] tokenize(String s) {
        return tokenize(s," \t\n\r\f");
    }
    public static String[] mapToEnv(Map<String,String> m) {
        String[] r = new String[m.size()];
        int idx=0;
        for (final Map.Entry<String,String> e : m.entrySet()) {
            r[idx++] = e.getKey() + '=' + e.getValue();
        }
        return r;
    }
    public static int min(int x, int... values) {
        for (int i : values) {

            if(i<x)




                x=i;
        }



        return x;
    }
    public static String nullify(String v) {

        return fixEmpty(v);
// 598 "Util1.java.merged"
    }
    public static String removeTrailingSlash(String s) {

        if(s.endsWith("/")) return s.substring(0,s.length()-1);
        else return s;







    }






    public static String getDigestOf(InputStream source) throws IOException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[1024];

            DigestInputStream in =new DigestInputStream(source,md5);
            try {

                while(in.read(buffer)>0)




                    ;
            } finally {
                in.close();
            }
            return toHexString(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IOException2("MD5 not installed",e);
        }
    }
    public static String getDigestOf(String text) {
        try {
            return getDigestOf(new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    public static SecretKey toAes128Key(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(s.getBytes("UTF-8"));
            return new SecretKeySpec(digest.digest(),0,128/8, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new Error(e);
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
    public static String toHexString(byte[] data, int start, int len) {
        StringBuilder buf = new StringBuilder();
        for( int i=0; i<len; i++ ) {
            int b = data[start+i]&0xFF;

            if(b<16) buf.append('0');






            buf.append(Integer.toHexString(b));
        }
        return buf.toString();
    }
    public static String toHexString(byte[] bytes) {
        return toHexString(bytes,0,bytes.length);
    }
    public static byte[] fromHexString(String data) {
        byte[] r = new byte[data.length() / 2];

        for (int i = 0; i < data.length(); i += 2)




            r[i / 2] = (byte) Integer.parseInt(data.substring(i, i + 2), 16);



        return r;
    }
    public static String getTimeSpanString(long duration) {
        long years = duration / ONE_YEAR_MS;
        duration %= ONE_YEAR_MS;
        long months = duration / ONE_MONTH_MS;
        duration %= ONE_MONTH_MS;
        long days = duration / ONE_DAY_MS;
        duration %= ONE_DAY_MS;
        long hours = duration / ONE_HOUR_MS;
        duration %= ONE_HOUR_MS;
        long minutes = duration / ONE_MINUTE_MS;
        duration %= ONE_MINUTE_MS;
        long seconds = duration / ONE_SECOND_MS;
        duration %= ONE_SECOND_MS;
        long millisecs = duration;

        if (years > 0)




            return makeTimeSpanString(years, Messages.Util_year(years), months, Messages.Util_month(months));

        else if (months > 0)




            return makeTimeSpanString(months, Messages.Util_month(months), days, Messages.Util_day(days));

        else if (days > 0)




            return makeTimeSpanString(days, Messages.Util_day(days), hours, Messages.Util_hour(hours));

        else if (hours > 0)




            return makeTimeSpanString(hours, Messages.Util_hour(hours), minutes, Messages.Util_minute(minutes));

        else if (minutes > 0)




            return makeTimeSpanString(minutes, Messages.Util_minute(minutes), seconds, Messages.Util_second(seconds));

        else if (seconds >= 10)




            return Messages.Util_second(seconds);

        else if (seconds >= 1)




            return Messages.Util_second(seconds+(float)(millisecs/100)/10);

        else if(millisecs>=100)




            return Messages.Util_second((float)(millisecs/10)/100);

        else




            return Messages.Util_millisecond(millisecs);
    }



    private static String makeTimeSpanString(long bigUnit,
                                             String bigLabel,
                                             long smallUnit,
                                             String smallLabel) {
        String text = bigLabel;

        if (bigUnit < 10)




            text += ' ' + smallLabel;



        return text;
    }
    public static String getPastTimeString(long duration) {
        return Messages.Util_pastTime(getTimeSpanString(duration));
    }
    public static String combine(long n, String suffix) {
        String s = Long.toString(n)+' '+suffix;
        if(n!=1)



            s += "s";



        return s;
    }
    public static <T> List<T> createSubList( Collection<?> source, Class<T> type ) {
        List<T> r = new ArrayList<T>();
        for (Object item : source) {

            if(type.isInstance(item))




                r.add(type.cast(item));
        }



        return r;
    }
    public static String encode(String s) {
        try {
            boolean escaped = false;
            StringBuilder out = new StringBuilder(s.length());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            OutputStreamWriter w = new OutputStreamWriter(buf,"UTF-8");
            for (int i = 0; i < s.length(); i++) {

                int c = s.charAt(i);




                if (c<128 && c!=' ') {
                    out.append((char) c);
                } else {
                    w.write(c);
                    w.flush();
                    for (byte b : buf.toByteArray()) {
                        out.append('%');
                        out.append(toDigit((b >> 4) & 0xF));
                        out.append(toDigit(b & 0xF));
                    }
                    buf.reset();
                    escaped = true;
                }
            }
            return escaped ? out.toString() : s;
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    private static final boolean[] uriMap = new boolean[123];
    static {
        String raw =
    "!  $ &'()*+,-. 0123456789   =  @ABCDEFGHIJKLMNOPQRSTUVWXYZ    _ abcdefghijklmnopqrstuvwxyz";
        int i;

        for (i = 0; i < 33; i++) uriMap[i] = true;
        for (int j = 0; j < raw.length(); i++, j++)







            uriMap[i] = (raw.charAt(j) == ' ');
    }



    public static String rawEncode(String s) {
        boolean escaped = false;
        StringBuilder out = null;
        CharsetEncoder enc = null;
        CharBuffer buf = null;
        char c;
        for (int i = 0, m = s.length(); i < m; i++) {
            c = s.charAt(i);
            if (c > 122 || uriMap[c]) {
                if (!escaped) {
                    out = new StringBuilder(i + (m - i) * 3);
                    out.append(s.substring(0, i));
                    enc = Charset.forName("UTF-8").newEncoder();
                    buf = CharBuffer.allocate(1);
                    escaped = true;
                }
                buf.put(0,c);
                buf.rewind();
                try {
                    ByteBuffer bytes = enc.encode(buf);
                    while (bytes.hasRemaining()) {
                        byte b = bytes.get();
                        out.append('%');
                        out.append(toDigit((b >> 4) & 0xF));
                        out.append(toDigit(b & 0xF));
                    }

                } catch (CharacterCodingException ex) { }





            } else if (escaped) {
                out.append(c);
            }
        }
        return escaped ? out.toString() : s;
    }
    private static char toDigit(int n) {
        return (char)(n < 10 ? '0' + n : 'A' + n - 10);
    }
    public static String singleQuote(String s) {
        return '\''+s+'\'';
    }
    public static String escape(String text) {

        if (text==null) return null;






        StringBuilder buf = new StringBuilder(text.length()+64);
        for( int i=0; i<text.length(); i++ ) {
            char ch = text.charAt(i);

            if(ch=='\n')




                buf.append("<br>");

            else
            if(ch=='<')




                buf.append("&lt;");

            else
            if(ch=='&')




                buf.append("&amp;");

            else


            if(ch=='"')





                buf.append("&quot;");


            else
            if(ch=='\'')





                buf.append("&#039;");


            else


            if(ch==' ') {
// 989 "Util1.java.merged"
                char nextCh = i+1 < text.length() ? text.charAt(i+1) : 0;
                buf.append(nextCh==' ' ? "&nbsp;" : " ");

            }
            else




                buf.append(ch);
        }



        return buf.toString();
    }
    public static String xmlEscape(String text) {
        StringBuilder buf = new StringBuilder(text.length()+64);
        for( int i=0; i<text.length(); i++ ) {
            char ch = text.charAt(i);

            if(ch=='<')




                buf.append("&lt;");

            else
            if(ch=='&')




                buf.append("&amp;");

            else
// 1069 "Util1.java.merged"
                buf.append(ch);
        }





        return buf.toString();
    }
// 1086 "Util1.java.merged"
    public static void touch(File file) throws IOException {
        new FileOutputStream(file).close();
    }





    public static void copyFile(File src, File dst) throws BuildException {
        Copy cp = new Copy();
        cp.setProject(new org.apache.tools.ant.Project());
        cp.setTofile(dst);
        cp.setFile(src);
        cp.setOverwrite(true);
        cp.execute();
    }





    public static String fixNull(String s) {

        if(s==null) return "";
        else return s;
// 1122 "Util1.java.merged"
    }






    public static String fixEmpty(String s) {

        if(s==null || s.length()==0) return null;






        return s;
    }







    public static String fixEmptyAndTrim(String s) {

        if(s==null) return null;






        return fixEmpty(s.trim());
    }
    public static <T> List<T> fixNull(List<T> l) {
        return l!=null ? l : Collections.<T>emptyList();
    }
    public static <T> Set<T> fixNull(Set<T> l) {
        return l!=null ? l : Collections.<T>emptySet();
    }
    public static <T> Collection<T> fixNull(Collection<T> l) {
        return l!=null ? l : Collections.<T>emptySet();
    }

    public static <T> Iterable<T> fixNull(Iterable<T> l) {
        return l!=null ? l : Collections.<T>emptySet();
    }




    public static String getFileName(String filePath) {
        int idx = filePath.lastIndexOf('\\');

        if(idx>=0)




            return getFileName(filePath.substring(idx+1));



        idx = filePath.lastIndexOf('/');

        if(idx>=0)




            return getFileName(filePath.substring(idx+1));



        return filePath;
    }





    public static String join(Collection<?> strings, String separator) {
        StringBuilder buf = new StringBuilder();
        boolean first=true;
        for (Object s : strings) {

            if(first) first=false;
            else buf.append(separator);
// 1220 "Util1.java.merged"
            buf.append(s);
        }
        return buf.toString();
    }





    public static <T> List<T> join(Collection<? extends T>... items) {
        int size = 0;

        for (Collection<? extends T> item : items)




            size += item.size();



        List<T> r = new ArrayList<T>(size);

        for (Collection<? extends T> item : items)




            r.addAll(item);



        return r;
    }
// 1422 "Util1.java.merged"
                                                                          )
                        return null;
                    throw new IOException("Failed to readlink "+link+" error="+ err+" "+ LIBC.strerror(err));







                }

                if (r==sz)
                    continue;
                byte[] buf = new byte[r];
                m.read(0,buf,0,r);
                return new String(buf);





            }

            throw new IOException("Symlink too long: "+link);
        } catch (LinkageError e) {
            return PosixAPI.get().readlink(filename);

        }
// 1471 "Util1.java.merged"
    }
// 1484 "Util1.java.merged"
    @Deprecated
    public static String encodeRFC2396(String url) {
        try {
            return new URI(null,url,null).toASCIIString();
        } catch (URISyntaxException e) {
            LOGGER.warning("Failed to encode "+url);
            return url;
        }
    }
    public static String wrapToErrorSpan(String s) {

        s = "<span class=error><img src='"+


            Stapler.getCurrentRequest().getContextPath()+ Jenkins.RESOURCE_PATH+





            "/images/none.gif' height=16 width=1>"+s+"</span>";






        return s;
    }
    public static Number tryParseNumber(String numberStr, Number defaultNumber) {
        if ((numberStr == null) || (numberStr.length() == 0)) {
            return defaultNumber;
        }
        try {
            return NumberFormat.getNumberInstance().parse(numberStr);
        } catch (ParseException e) {
            return defaultNumber;
        }
    }
    public static boolean isOverridden(Class base, Class derived, String methodName, Class... types) {
        try {
            return !base.getMethod(methodName, types).equals(
                    derived.getMethod(methodName,types));
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
    }
    public static File changeExtension(File dst, String ext) {
        String p = dst.getPath();
        int pos = p.lastIndexOf('.');

        if (pos<0) return new File(p+ext);
        else return new File(p.substring(0,pos)+ext);
// 1548 "Util1.java.merged"
    }





    public static String intern(String s) {
        return s==null ? s : s.intern();
    }


    @IgnoreJRERequirement


    public static Properties loadProperties(String properties) throws IOException {
        Properties p = new Properties();
        try {
            p.load(new StringReader(properties));
        } catch (NoSuchMethodError e) {
            p.load(new ByteArrayInputStream(properties.getBytes()));
        }
        return p;
    }

    public static final FastDateFormat XS_DATETIME_FORMATTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'",new SimpleTimeZone(0,"GMT"));

    public static final FastDateFormat RFC822_DATETIME_FORMATTER
            = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);




    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
    public static boolean NO_SYMLINK = Boolean.getBoolean(Util.class.getName()+".noSymLink");
    public static boolean SYMLINK_ESCAPEHATCH = Boolean.getBoolean(Util.class.getName()+".symlinkEscapeHatch");
}