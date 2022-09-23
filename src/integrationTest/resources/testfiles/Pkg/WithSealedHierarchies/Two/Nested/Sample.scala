package dummy

sealed trait Sample1

class Sample1A extends Sample1

class Sample1B extends Sample1

sealed trait Sample2 extends Sample1

class Sample2A extends Sample2

class Sample2B extends Sample2
