# Barcode-Scanning-ML

    ```With ML-Kit Vision API CameraX,
    barcodes.forEach { barcode ->
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        binding.url.text=barcode.url.toString()
                    }
                    Barcode.FORMAT_QR_CODE -> {
                        binding.qr.text=barcode.format.toString()
                    }
                    Barcode.TYPE_EMAIL -> {
                        binding.email.text=barcode.email.toString()
                    }
                    Barcode.TYPE_CONTACT_INFO -> {
                        binding.contact.text=barcode.contactInfo.toString()
                    }
                    Barcode.TYPE_WIFI -> {
                        binding.wifi.text=barcode.contactInfo.toString()
                    }
                    else -> {
                        binding.raw.text=barcode.rawValue.toString()
                    }
                }

                <img width="258" alt="image" src="https://github.com/karun02525/Barcode-Scanning-ML/assets/36824081/5c598972-5066-4d7b-9ef7-3291634f88be">
                <img width="364" alt="image" src="https://github.com/karun02525/Barcode-Scanning-ML/assets/36824081/82a3ae85-769b-4e30-a642-9eeb540ccfc4">
                <img width="308" alt="image" src="https://github.com/karun02525/Barcode-Scanning-ML/assets/36824081/4fa64f7d-f819-4a8b-9cc4-1c6bdfefd961">



    
