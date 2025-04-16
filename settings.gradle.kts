rootProject.name = "DamageIndicator"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.4.0")
}

include(
    "API",
    "DamageIndicator",
    ":NMS:1_16_R3",
    ":NMS:1_17_R1",
    ":NMS:1_18_R1",
    ":NMS:1_19_R1",
    ":NMS:1_19_R2",
    ":NMS:1_19_R3",
    ":NMS:1_20_R1",
    ":NMS:1_20_R2",
    ":NMS:1_20_R3",
    ":NMS:1_20_R4",
    ":NMS:1_21_R1",
    ":NMS:1_21_R2",
    ":NMS:1_21_R3",
    ":NMS:1_21_R4",
)

