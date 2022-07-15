- [コメントビューアー制作](#コメントビューアー制作)
    - [WebSocket サーバー接続方法参考 URL](#websocket-サーバー接続方法参考-url)
- [学んだこと](#学んだこと)
- [作成手順](#作成手順)
    - [WebSocket 接続で気をつけるなければならなかったこと](#websocket-接続で気をつけるなければならなかったこと)
    - [その 2](#その-2)

# コメントビューアー制作

## WebSocket サーバー接続方法参考 URL

https://qiita.com/pasta04/items/c22933066154b33ae7e0

# 学んだこと

Kotlin コルーチン  
OkHttp3 を用いた HTTP GET メソッド、WebSocket 接続

# 作成手順

今回の目的は android アプリで Openrec のコメントを取得することだ。  
これには配信ページから手順を踏んで WebSocket サーバーの URL を手に入れる必要がある。

だが Android アプリで WebSocket 接続ができなければ、今回のアプリを作る意味がない。  
よって、ブラウザの検証機能を用いて Network → WS から WebSocket サーバーの URL を直接コピーしてきて WebSocket 接続ができるかを試す。

WebSocket 接続には `OkHttp3` というパッケージを利用する。  
選定理由は、

- WebSocket 接続ができる。
- HTTP 接続もできる。

この 2 つが 1 つのパッケージで済むからだ。

## WebSocket 接続で気をつけるなければならなかったこと

WebSocket サーバーから 2 分弱で強制的にコメント受信を切断されて困っていた。  
調べてみると WebSocket は双方向間で通信するので、クライアント（通常はブラウザ）から応答がなかった場合に、サーバー側から自動的に接続を切るためPing Pong というものを用いている。

参考 URL にて `WebSocket サーバーに対して Ping を送信する` と書かれている。

今回は OkHttp というパッケージを用いて Ping を送ることも可能らしい。  
https://zenn.dev/cizneeh/articles/websocket-on-android


しかし今回は WebSocket で Ping を利用するのではなく、send メソッドを用いる。

が、おそらく Openrec 側では Ping Pong 機能を利用してサーバー側からクライアントを切断する判断をしているのではなく、send で判断していると思われる。

## その 2

https://scrapbox.io/shokai/Socket.IO%E3%81%AE%E5%88%87%E6%96%AD%E6%A4%9C%E7%9F%A5%E3%81%AF%E3%83%89%E3%82%AD%E3%83%A5%E3%83%A1%E3%83%B3%E3%83%88%E3%81%A8%E5%AE%9F%E8%A3%85%E3%81%8C%E9%81%95%E3%81%86

このサイトは socket.io のサーバーに関して記載していると思う。  
Openrec も WebSocket の URL 的におそらく socket.io を用いていると思われる。

そしてサイトにて `pingInterval: 25000, pingTimeout: 60000` と記述されている。（単位はミリセカンド）  
これは Openrec サーバーの設定と完全に一致している。  
このサイトによると socket.io で作成したサーバーは、この 2 つを足し合わせた数字の時間分経っても応答がないとクライアントを切断するらしい。

ただ、Chrome の検証機能を利用し Network の WS から通信を見てみると 25 秒毎にクライアントから "2" を送信しているのがわかる。  
よって 85 秒までなら切断されないかもしれないが、今回のアプリでも 25 秒毎に "2" を送信する。  