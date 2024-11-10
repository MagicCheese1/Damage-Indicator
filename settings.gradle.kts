rootProject.name = "DamageIndicator"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}

include(
    "API",
    "DamageIndicator",
    "1_16_R3",
    "1_17_R1",
    "1_18_R1",
    "1_19_R1",
    "1_19_R2",
    "1_19_R3",
    "1_20_R1",
    "1_20_R2",
    "1_20_R3",
)

