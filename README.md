# Currency Exchanger

REST API для создания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, а также рассчитывать конвертацию любых сумм из одной валюты в другую.

##  🚀  API Endpoints

**Currencies**

`GET /currencies`
Получение всех валют
```json
[
    {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },   
    {
        "id": 0,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    }
]
```

`GET /currency/EUR`
Получение конкретной валюты
```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```

`POST /currencies`
Добавление новой валюты
```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}

```
