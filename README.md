Pig ERG Analyzer
Hey! This is the program for analyzing the pig ERG data, A few prerequisites for it is python and java need to be downloaded on your computer in order for the code to work, so if you dont have them downloaded already, the links to the downloads are listed below.
Downloads for MacOS:
https://www.java.com/en/download/apple.jsp
https://www.python.org/downloads/macos/
Downloads for Windows:
https://www.java.com/download/ie_manual.jsp
https://www.python.org/downloads/windows/

To launch the program just double click on the Launch_Program file.
	
Once launched, a pop-up window will ask you to first drag and drop your input excel file, here you drag and drop the excel file with the data you want to be analyzed, then the window will ask you to drag and drop your output excel file, here you drag and drop the excel file you wish the organized data to be put into. After a couple of seconds, the window will close, and you should have the analyzed data in your output excel file!

Let me know if you are having issues or need the analysis tweaked or modified for extra utility, it wont take long to edit it!

P.S. Attached I have an example excel file for the general format the data should be in.


PATCH NOTES FOR V1:
 - Fixed Error with empty sheets in the workbook, now program still runs with empty sheets.
 - Reformatted output file to sort data points by pig type instead of eye orientation(hopefully easier for any             additional data entry required).
 - Added comments to the raw java code for better readability and transparency in how the analysis is carried out
 - Added a drag and drop window for file input instead of command prompt for more user-friendly experience.
 - Template excel file added for more intuitive and correct formatting of data.
 - General tidying of source code and minor bug fixes.

V1.1 PATCH NOTES:
 - Reformatted program to account for lack of smoothed OR raw waveform values.