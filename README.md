# TransLinkMe

## What does this do?

TransLinkMe provides a way for the user to store bus stop locations for easy access of bus stop times.

If you want to find out bus stop arrivals at a particular bus stop by texting, you have to text the bus stop and bus number.

```
Please text [stop#] [bus#]
for Next Bus times. Buses at
##### are: ###, ###
```

TransLinkMe allows you to just enter the bus stop number and be able to see the arrival times for all buses that come by. You can save bus stops and favourite them for later so you don't have to search for the bus number again. :raised_hands:

## Screenshots

Soon :grin:

## Demos

Soon :grin:

## Getting Started

### Prerequisites

* A TransLink Open API Developer Key

```
https://developer.translink.ca/Account/Register
```

### Installation

Use your copy of a TransLink Open API Developer Key :wink:

Your API Key will authorize you to offer a maximum of 1,000 requests per day for use of the Data. TransLink reserves the right, at any time after the API Key is issued, to limit the number of maximum requests in any one day. 

* Navigate to translinkme_app/utils/Constants.java in the project

```
Replace the constant TRANSLINK_OPEN_API_KEY with yours. 
```

## Built With

### APIs

* [TransLink Open API](https://developer.translink.ca/) - Used to get transit information.

### Libraries

* [ButterKnife](https://github.com/JakeWharton/butterknife) - Used to bind Android views and callbacks to fields and methods.
* [Lottie](https://github.com/airbnb/lottie-android) - Used to render After Effects animations natively on Android and iOS.
* [Toasty](https://github.com/GrenderG/Toasty) - Used for the usual toasts, but with steroids.

## License

```
MIT License

Copyright (c) 2017 Josh Vocal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

```

