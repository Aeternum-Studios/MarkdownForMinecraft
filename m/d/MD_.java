package m.d;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MD_ extends JavaPlugin implements Listener {

//                             _   _____ 
//                            | | | ____|
//               _ __ ___   __| | | |__  
//              | '_ ` _ \ / _` | |___ \ 
//              | | | | | | (_| |  ___) |
//              |_| |_| |_|\__,_| |____/ 
//                            ______     
//                           |______|    
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents( this, this );
		saveResource( "README.md", true );
	}

	@EventHandler( priority = EventPriority.HIGHEST )
	public void onAsyncChatEvent( AsyncPlayerChatEvent event ) {
		event.setMessage( parseMarkdown( event.getPlayer(), event.getMessage() ) );
	}

	public static String parseMarkdown( Player player, String message ) {
		String translated = message;
		
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.bold" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)\\*\\*", ChatColor.COLOR_CHAR + "z", ChatColor.COLOR_CHAR + "Z" );
		}
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.italic" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)\\*", ChatColor.COLOR_CHAR + "x", ChatColor.COLOR_CHAR + "X" );
		}
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.underline" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)__", ChatColor.COLOR_CHAR + "v", ChatColor.COLOR_CHAR + "V" );
		}
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.italic" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)_", ChatColor.COLOR_CHAR + "q", ChatColor.COLOR_CHAR + "Q" );
		}
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.strikethrough" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)~~", ChatColor.COLOR_CHAR + "m", ChatColor.COLOR_CHAR + "M" );
		}
		if ( player.hasPermission( "md.parse.all" ) || player.hasPermission( "md.parse.magic" ) ) {
			translated = replaceWith( translated, "(?<!\\\\)~", ChatColor.COLOR_CHAR + "w", ChatColor.COLOR_CHAR + "W" );
		}

		translated = translated.replace( "\\*", "*" ).replace( "\\_", "_" ).replace( "\\~", "~" );
		
		String[] parts = ( " " + translated ).split( "" + ChatColor.COLOR_CHAR );
		StringBuilder builder = new StringBuilder();
		for ( String part : parts ) {
			if ( part.isEmpty() ) {
				continue;
			}
			char colorCharacter = part.charAt( 0 );
			ChatColor color = ChatColor.getByChar( colorCharacter );
			
			String colors = ChatColor.getLastColors( builder.toString() );
			if ( color != null ) {
				StringBuilder colorBuilder = new StringBuilder();
				for ( String cc : colors.split( ChatColor.COLOR_CHAR + "" ) ) {
					if ( cc.isEmpty() ) {
						continue;
					}
					if ( ChatColor.getByChar( cc.charAt( 0 ) ).isFormat() ) {
						colorBuilder.append( ChatColor.COLOR_CHAR + cc );
					}
				}
				builder.append( color + colorBuilder.toString() );
			} else {
				if ( colorCharacter == 'z' ) {
					builder.append( ChatColor.BOLD );
				} else if ( colorCharacter == 'x' ) {
					builder.append( ChatColor.ITALIC );
				} else if ( colorCharacter == 'v' ) {
					builder.append( ChatColor.UNDERLINE );
				} else if ( colorCharacter == 'q' ) {
					builder.append( ChatColor.ITALIC );
				} else if ( colorCharacter == 'm' ) {
					builder.append( ChatColor.STRIKETHROUGH );
				} else if ( colorCharacter == 'w' ) {
					builder.append( ChatColor.MAGIC );
				} else if ( colorCharacter == 'Z' ) {
					colors = colors.replace( ChatColor.BOLD.toString(), "" );
				} else if ( colorCharacter == 'X' ) {
					colors = colors.replace( ChatColor.ITALIC.toString(), "" );
				} else if ( colorCharacter == 'V' ) {
					colors = colors.replace( ChatColor.UNDERLINE.toString(), "" );
				} else if ( colorCharacter == 'Q' ) {
					colors = colors.replace( ChatColor.ITALIC.toString(), "" );
				} else if ( colorCharacter == 'M' ) {
					colors = colors.replace( ChatColor.STRIKETHROUGH.toString(), "" );
				} else if ( colorCharacter == 'W' ) {
					colors = colors.replace( ChatColor.MAGIC.toString(), "" );
				}
				if ( Character.isUpperCase( colorCharacter ) ) {
					builder.append( ChatColor.RESET + colors );
				}
			}
			if ( part.length() > 1 ) {
				builder.append( part.substring( 1 ) );
			}
		}

		return builder.toString();
	}

	private static String replaceWith( String message, String quot, String pre, String suf ) {
		String part = message;
		for ( String str : getMatches( message, quot + "(.+?)" + quot ) ) {
			part = part.replaceFirst( quot + Pattern.quote( str ) + quot, pre + str + suf );
		}
		return part;
	}

	public static List< String > getMatches( String string, String regex ) {
		Pattern pattern = Pattern.compile( regex );
		Matcher matcher = pattern.matcher( string );
		List< String > matches = new ArrayList< String >();
		while ( matcher.find() ) {
			matches.add( matcher.group( 1 ) );
		}
		return matches;
	}
}
