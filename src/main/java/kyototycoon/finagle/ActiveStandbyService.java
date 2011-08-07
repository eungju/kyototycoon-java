package kyototycoon.finagle;

import com.twitter.finagle.NotServableException;
import com.twitter.finagle.Service;
import com.twitter.util.Future;

import java.util.List;

public class ActiveStandbyService<Req, Rep> extends Service<Req, Rep> {
    private List<Service<Req, Rep>> underlying;

    public ActiveStandbyService(List<Service<Req, Rep>> underlying) {
        this.underlying = underlying;
    }

//    Future<Rep> dispatch(Req req, List<Service<Req, Rep>> services) {
//        services match {
//          case service :: rest =>
//            // more sophisticated uses may make this conditional
//            // on the type of exception
//            service(req) rescue { _ => dispatch(req, rest) }
//          case None =>
//            Future.exception(new NoMoreServersAvailableException)
//        }
//      }
//
//  def apply(req: Request) = dispatch(req, underlying)
//
//  def release() = underlying foreach { _.release() }

    public Future<Rep> apply(Req req) {
        for (Service<Req, Rep> service : underlying) {
            if (service.isAvailable()) {
                return service.apply(req);
            }
        }
        return Future.exception(new NotServableException());
    }

    public void release() {
        for (Service<Req, Rep> service : underlying) {
            service.release();
        }
    }

    public boolean isAvailable() {
        for (Service<Req, Rep> service : underlying) {
            if (service.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}


