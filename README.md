# Damage Indicator

This is a plugin for Minecraft Bukkit/Spigot/Paper servers. It adds an indicators for how much damage you dealt to an entity.
![](/images/dmgindicatorscreen.png)

## Installing

1. Download the newest release from the [releases page](https://github.com/MagicCheese1/Damage-Indicator/releases)

2. Put the downloaded jar in your plugins folder

3. Enjoy

## Configuration

You can find the config file at \plugins\DamageIndicator\config.yml. By default it should look like this:

```yaml
#Should the indicator be shown to everyone(true) or just the damager(false)
ShowToDamagerOnly: true

#The format for showing the damage (example: "&7-0.0#&4❤")
#https://docs.oracle.com/javase/7/docs/api/java/text/DecimalFormat.html
IndicatorFormat: "&7-0.#&4❤"
CriticalIndicatorFormat: "&c-0.#&4❤"
# Use Second
IndicatorTime: 1.5
```

> Hex colours are supported using the legacy format (e.g. §x§F§F§F§F§F§F to represent white), but may also be defined
> using a smaller version unique to the damage indicator (e.g. §#FFFFFF to represent white).
> 
> Note that the usage of 0's in the hex code would mess with the decimal formatting, hence they should be escaped using
> single quotes, such as this:
> ```yaml
> IndicatorFormat: "'§#FFC300'-0.#&4❤"
> ```

## Commands and permissions

`/damageindicator reload` reloads the config. requires `Damageindicator.admin` 


## Building

1. Make sure you have [Maven](https://maven.apache.org/) installed
2. Clone this repo
3. open a terminal and type `mvn package`
4. The jar file is in the /target/ folder

## License

This repo is licensed under the [MIT LICENSE](/LICENSE)
