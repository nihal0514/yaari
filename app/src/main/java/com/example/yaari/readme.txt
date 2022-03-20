pehle ham data ko (viewmodel.observe) etc se call karenge toh woh pehle viewmodel class ke  pass jaayga(e.g. MainViewModel,profileViewModel),
uske baad woh repository mai jaayega aur Call<> karega.


viewmodel(e.g. MainViewModel,profileViewModel) ka saathidaar viewmodelfactory hai jaha jaha viewmodel ka use hoga waha waha viewmodelfactory mai changes honge.