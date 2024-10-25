# Setup

1. Install repo
2. Setup up database (btw in see application_template.properties to make application.properties)
3. Execute this SQL-command:
```
CREATE TABLE launch_pool_info (
    id SERIAL PRIMARY KEY,
    exchange VARCHAR(255),
    launch_pool VARCHAR(255),
    pools TEXT,
    period VARCHAR(255),
    status VARCHAR(255)
);

INSERT INTO launch_pool_info (exchange, launch_pool, pools, period, status) VALUES 
('Bybit', 'DBR', '{MNT:=800%, USDT:=800%, DBR:=800%}', '17.10 08:00 — 24.10 08:00 UTC', 'Скоро почнеться'),
('Bitget', 'PUFFER', '{PUFFER:=0%}', '16.10 12:00 — 26.10 12:00 UTC', 'Скоро почнеться'),
('Gate', 'AIX', '{USDT:=0%, GT:=0%}', '16.10 05:00 — 23.10 05:00 UTC', 'Скоро почнеться'),
('Gate', 'CYRUS', '{USDT:=0%, GT:=0%}', '15.10 06:00 — 22.10 06:00 UTC', 'Скоро почнеться'),
('Bybit', 'PUFFER', '{PUFFER:=800%, MNT:=352.7%, USDT:=186.67%}', '14.10 12:00 — 21.10 12:00 UTC', 'Активний'),
('Gate', 'PUFFER', '{PUFFER:=5073.87%, GT:=25.76%}', '14.10 12:00 — 21.10 12:00 UTC', 'Активний'),
('Bitget', 'PUFFER', '{USDT:=984.49%, BGB=}', '14.10 12:00 — 21.10 12:00 UTC', 'Активний'),
('Kucoin', 'PUFFER', '{PUFFER=, USDT=, KCS=}', '14.10 12:00 — 21.10 12:00 UTC', 'Активний'),
('Kucoin', 'DEEP', '{USDT=, KCS=}', '14.10 10:00 — 21.10 10:00 UTC', 'Активний'),
('Gate', 'GEEK', '{GT:=0.89%, GEEK:=1421.95%}', '11.10 10:00 — 16.10 10:00 UTC', 'Активний'),
('Bitget', 'CARV', '{CARV:=240.95%, USDT:=87.36%}', '10.10 08:00 — 20.10 08:00 UTC', 'Активний'),
('Bitget', 'OGLG', '{BTC:=1.32%}', '09.10 12:00 — 19.10 12:00 UTC', 'Активний'),
('Bitget', 'CATS', '{CATS:=141.95%}', '08.10 10:00 — 18.10 10:00 UTC', 'Активний'),
('Bybit', 'CATS', '{CATS:=1158.81%, MNT:=66.15%, USDT:=41.42%}', '08.10 10:00 — 15.10 10:00 UTC', 'Активний'),
('Gate', 'CATS', '{CATS:=1012.63%, GT:=44.92%}', '08.10 10:00 — 15.10 10:00 UTC', 'Активний');
```
4. Check information in database:
```
SELECT * FROM launch_pool_info
 ```