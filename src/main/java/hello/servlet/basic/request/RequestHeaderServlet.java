package hello.servlet.basic.request;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet  extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        printStartLine(req);
        printHeaders(req);
    }
    private void printStartLine(HttpServletRequest  req){
        System.out.println("req.getMethod() = " + req.getMethod()); //GET
        System.out.println("req.getProtocol() = " + req.getProtocol()); //HTTP 1.1
        System.out.println("req.getScheme() = " + req.getScheme());
        System.out.println("req.getRequestURL() = " + req.getRequestURL());
        System.out.println("req.getRequestURI() = " + req.getRequestURI());
        System.out.println("req.getQueryString()= " + req.getQueryString());

        System.out.println("req.isSecure() = " + req.isSecure());

        System.out.println();
    }

    //Header 모든 정보
    private void printHeaders(HttpServletRequest request){
        System.out.println("---- Headers - start ---");

        Enumeration<String> headerNames = request.getHeaderNames();
//        while(getInitParameterNames().hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            System.out.println(headerName + ": = " + headerName );
//        }
        //위와 동일하게 사용이 가능하다.
        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + ": "+headerName));

        System.out.println("--- header - end ----");
        System.out.println();
    }

}
