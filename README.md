# Damage indicator

This is a plugin for Minecraft Paper servers. It adds an indicator for how much damage you dealt to an entity.
![](/images/dmgindicatorscreen.png)

## dependencies

- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

## Installing

1. Download the newest release from the [releases page](https://github.com/MagicCheese1/Damage-Indicator/releases)

2. Download the dependencies

3. Put everything in your plugins folder

4. Enjoy

## Configuration

You can find the config file at \plugins\DamageIndicator\config.yml. By default it should look like this:

```yaml
#The indicator color for normal hits
HitColor: "7"

#The indicator color for critical hits
CriticalHitColor: "4"

#Should the indicator be shown to everyone(true) or just the damager(false)
#! DON"T RELOAD THE SERVER AFTER CHANGING THIS VALUE INSTEAD RESTART
ShowToDamagerOnly: true

#The format for showing the damage (example: "0.0#")
#https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html
IndicatorFormat: "0.#"
```

## Building

1. Make sure you have [Maven](https://maven.apache.org/) installed
2. Clone this repo
3. open a terminal and type `mvn install`
4. The jar file is in the /target/ folder

## License

This repo is licensed under the [MIT LICENSE](/LICENSE)
