# 💵 Currency Exchanger

REST API для создания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, а также рассчитывать конвертацию любых сумм из одной валюты в другую.

##  🚀  API Endpoints

### Currencies

`GET /currencies`

Получение всех валют. Пример ответа: 
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

Получение конкретной валюты по ее коду. Пример ответа: 
```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```

`POST /currencies`

Добавление новой валюты, данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Пример ответа: 
```json
{
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}

```

### ExchangeRates

`GET /exchangeRates`

Получение списка всех обменных курсов. Пример ответа:
```json
[
    {
        "id": 0,
        "baseCurrency": {
            "id": 0,
            "name": "United States dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "rate": 0.99
    }
]

```

`GET /exchangeRate/USDRUB`

Получение конкретного обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Пример ответа:
```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}

```


`POST /exchangeRates `

Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - baseCurrencyCode, targetCurrencyCode, rate. Пример ответа:
```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}

```

`PATCH /exchangeRate/USDRUB`

Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Единственное поле формы - rate. Пример ответа:
```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
}

```

### Currency exchange

`GET /exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Расчёт перевода определённого количества средств из одной валюты в другую. Пример запроса - GET /exchange?from=USD&to=AUD&amount=10. Пример ответа:

```json
{
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A$"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
}
```
