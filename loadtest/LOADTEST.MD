# 3 этап - тестирование с Нагрузкой

Для подачи нагрузки на KV сервис был использован wrk, 

## тестирование до оптимизаций

### PUT без перезаписи

#### 2 threads, 2 connections:
```sh
wrk --latency -t2 -c2 -d1m -s put_no_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 2 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.07ms  154.44ms   1.36s    94.45%
    Req/Sec     1.34k   273.13     1.60k    88.49%
  Latency Distribution
     50%  258.00us
     75%  358.00us
     90%  776.00us
     99%  887.41ms
  149602 requests in 1.00m, 13.41MB read
Requests/sec:   2491.25
Transfer/sec:    228.69KB

```

---

#### 2 threads, 4 connections:
```sh
wrk --latency -t2 -c4 -d1m -s put_no_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.25ms  144.07ms   1.21s    93.73%
    Req/Sec     1.72k   312.50     1.99k    93.01%
  Latency Distribution
     50%  601.00us
     75%  715.00us
     90%    1.72ms
     99%  824.23ms
  190249 requests in 1.00m, 17.05MB read
Requests/sec:   3168.85
Transfer/sec:    290.89KB
```

---

#### 4 threads, 4 connections:
```sh
wrk --latency -t4 -c4 -d1m -s put_no_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     9.90ms   50.94ms 659.44ms   95.82%
    Req/Sec     0.89k   137.34     1.03k    92.60%
  Latency Distribution
     50%  540.00us
     75%  692.00us
     90%    0.92ms
     99%  280.23ms
  207288 requests in 1.00m, 18.58MB read
Requests/sec:   3449.15
Transfer/sec:    316.62KB
```

---

### GET без повторов:

#### 2 threads, 2 connections
```sh
wrk --latency -t2 -c2 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 2 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   144.59us  168.70us  11.06ms   98.58%
    Req/Sec     6.80k   661.13     7.38k    88.85%
  Latency Distribution
     50%  126.00us
     75%  136.00us
     90%  167.00us
     99%  396.00us
  813601 requests in 1.00m, 76.41MB read
  Non-2xx or 3xx responses: 811602
Requests/sec:  13537.60
Transfer/sec:      1.27MB
```
---

#### 2 threads, 4 connections
```sh
wrk --latency -t2 -c4 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   306.88us  227.40us  11.04ms   90.79%
    Req/Sec     6.68k     1.60k    8.96k    70.33%
  Latency Distribution
     50%  263.00us
     75%  351.00us
     90%  484.00us
     99%    1.03ms
  798215 requests in 1.00m, 255.79MB read
  Non-2xx or 3xx responses: 607956
Requests/sec:  13303.18
Transfer/sec:      4.26MB

```
---
4 threads, 4 connections
```sh
wrk --latency -t4 -c4 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   355.14us  264.96us  14.58ms   90.57%
    Req/Sec     2.85k   785.65     4.10k    61.67%
  Latency Distribution
     50%  311.00us
     75%  432.00us
     90%  586.00us
     99%    1.02ms
  679777 requests in 1.00m, 427.67MB read
  Non-2xx or 3xx responses: 299258
Requests/sec:  11328.61
Transfer/sec:      7.13MB

```
---

### PUT c перезаписью

#### 2 threads, 2 connections
```sh
wrk --latency -t2 -c2 -d1m -s put_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 2 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   554.84us    3.38ms  83.27ms   99.05%
    Req/Sec     1.41k   169.43     1.55k    90.83%
  Latency Distribution
     50%  251.00us
     75%  308.00us
     90%  367.00us
     99%    3.12ms
  168862 requests in 1.00m, 15.14MB read
Requests/sec:   2813.68
Transfer/sec:    258.29KB
```
---
#### 2 threads, 4 connections
```sh
wrk --latency -t2 -c4 -d1m -s put_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   829.13us    1.65ms  47.69ms   98.34%
    Req/Sec     1.52k   295.13     1.97k    59.42%
  Latency Distribution
     50%  624.00us
     75%  741.00us
     90%    1.08ms
     99%    3.07ms
  181525 requests in 1.00m, 16.27MB read
Requests/sec:   3023.55
Transfer/sec:    277.55KB
```
---

#### 4 threads, 4 connections
```sh
wrk --latency -t4 -c4 -d1m -s put_rewriting.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     0.88ms    2.03ms  71.37ms   98.75%
    Req/Sec   761.27    137.17     0.97k    61.00%
  Latency Distribution
     50%  624.00us
     75%    0.86ms
     90%    1.29ms
     99%    3.35ms
  181940 requests in 1.00m, 16.31MB read
Requests/sec:   3030.15
Transfer/sec:    278.16KB
```
---

### GET c повторами:

#### 2 threads, 2 connections
```sh
wrk --latency -t2 -c4 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   268.31us  266.36us  16.85ms   98.22%
    Req/Sec     7.54k   701.76     8.21k    89.77%
  Latency Distribution
     50%  245.00us
     75%  310.00us
     90%  374.00us
     99%  694.00us
  902144 requests in 1.00m, 84.51MB read
  Non-2xx or 3xx responses: 900145
Requests/sec:  15010.21
Transfer/sec:      1.41MB
```
---

#### 2 threads, 4 connections
```sh
wrk --latency -t2 -c4 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   268.31us  266.36us  16.85ms   98.22%
    Req/Sec     7.54k   701.76     8.21k    89.77%
  Latency Distribution
     50%  245.00us
     75%  310.00us
     90%  374.00us
     99%  694.00us
  902144 requests in 1.00m, 84.51MB read
  Non-2xx or 3xx responses: 900145
Requests/sec:  15010.21
Transfer/sec:      1.41MB
```
---

#### 4 threads, 4 connections
```sh 
wrk --latency -t4 -c4 -d1m -s get_no_repeat.lua http://localhost:8080
Running 1m test @ http://localhost:8080
  4 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   302.12us  476.61us  23.00ms   98.23%
    Req/Sec     3.59k   581.96     4.26k    81.12%
  Latency Distribution
     50%  251.00us
     75%  328.00us
     90%  407.00us
     99%    1.13ms
  857737 requests in 1.00m, 82.37MB read
  Non-2xx or 3xx responses: 853738
Requests/sec:  14292.29
Transfer/sec:      1.37MB
```
---


### заключение
как я понимаю, такие результаты из-за особенности работы macOS, тесты с оптимизацией буду проводить на Linux и эти тоже переделаю.
