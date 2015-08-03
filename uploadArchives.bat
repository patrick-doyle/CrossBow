call gradlew copyReadme
call gradlew :Libraries:CrossBow:build :Libraries:CrossBow:bintrayUpload
call gradlew :Libraries:CrossBow-Wear:build :Libraries:CrossBow-Wear:bintrayUpload
call gradlew :Libraries:Crossbow-Wear-Receiver:build :Libraries:Crossbow-Wear-Receiver:bintrayUpload