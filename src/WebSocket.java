import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/ws")
public class WebSocket {
	Session sess;


    //今回追加する部分
    @OnOpen
    public void onOpen(Session session) {
        /* セッション確立時の処理 */
        System.out.println("WebSocketセッション確立");
        sess = session;
    }

    //今回追加する部分
    @OnMessage
    public void onMessage(String message) {
        /* メッセージ受信時の処理 */
        System.out.println("WebSocket受信：" + message);
        try {
			sess.getBasicRemote().sendText("OK");
			sess.getBasicRemote().sendText("OK2");
			sess.getBasicRemote().sendText("OK3");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    //今回追加する部分
    @OnError
    public void onError(Throwable th) {
        /* エラー発生時の処理 */
        System.out.println("WebSocketエラー：" + th.getMessage());
    }

    //今回追加する部分
    @OnClose
    public void onClose(Session session) {
        /* セッション解放時の処理 */
        System.out.println("WebSocketセッション終了");
    }
}
