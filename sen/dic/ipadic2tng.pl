#!/usr/bin/perl

my $PREFIX = shift (@ARGV) || ".";
$PREFIX =~ s#/$##g;

## sub routines
sub strip
{
    my $a = $_[0];
    $a =~ s/^ //g; 
    $a =~ s/ $//g; 
    $a =~ s/^\(//g; 
    $a =~ s/\)$//g; 
    return $a;
}

sub append 
{
    my ($a, $b) = @_;
    return $b if ($a eq ""); 
    return $a if ($b eq "*" || $b eq "");
    return $a . $b;
}

sub conv 
{
    my $str   = $_[0];
    my @tmp   = split /,/, $str;
    my $ctype = $tmp[6];

    # ³èÍÑ¤òÅ¸³«¤¹¤?É¬Í×¤¬Ìµ¤¤¤È¤­¤Ï, ¤½¤Î¤Þ¤Þ½ÐÎÏ    
    if (! defined $CTYPE{$ctype}) { 
	return "$str\n";
	next;
    }

    my $lex   = $tmp[0];
    my $base  = $tmp[8];
    my $read  = $tmp[9];
    my $pron  = $tmp[10]; 

    my $lexs  = $lex;
    my $reads = $read;
    my $prons = $pron;

    # ¸?´´¤À¤±»Ä¤¹
    my @list = @{$CTYPE{$ctype}};
    if ($list[0][1] ne "*") {
	$lexs  = substr ($lex,  0, length ($lex)  - length($list[0][1]));
	$reads = substr ($read, 0, length ($read) - length($list[0][2]));
	$prons = substr ($pron, 0, length ($pron) - length($list[0][2]));
    }

    # Å¸³«!
    my $result = "";
    for my $i (0..$#list) {
	my $cform   = $list[$i][0];
	my $newlex  = &append ($lexs,  $list[$i][1]);
	my $newread = &append ($reads, $list[$i][2]);
	my $newpron = &append ($prons, $list[$i][2]);
	next if (length ($newlex) <= 1);
	$result .= "$newlex,@tmp[1],$tmp[2],$tmp[3],$tmp[4],$tmp[5],$tmp[6],$cform,$base,$newread,$newpron\n";
    }

    return $result;
}


#########
# main

# connect.cha
open (F, "$PREFIX/connect.cha") || die "Fatal: $PREFIX/connect.cha cannot open\n";
open (S, "> connect.sen") || die "FATAL: connect.txt cannot open\n";
while (<F>) {
    chomp;

    s/ (\d+)\)//;
    my $score = $1;
    my @tmp = split /\) \(/, $_;

    my @out;
    for (@tmp) {
	my @pos;
	if (/\(+([^\)]+)\)/) {
	    @pos = split / /, $1;
	}

	my @other;
	if (/\)+ ([^\)]+)\)+$/) {
	    my $t = $1;
	    $t =~ s/^ //g;
	    $t =~ s/ $//g;
	    @other = split / /, $t;
	}

	for my $i ($#pos + 1 .. 3) {
	    $pos[$i] = "*";
	}

	for my $i ($#other + 1 .. 2) {
	    $other[$i] = "*";
	}

	push @pos, @other;
	push @out, join ",", @pos;
    }

    if (@out == 2) { 
	@out = ("*,*,*,*,*,*,*", @out);
    }

    print S "\"$out[0]\",\"$out[1]\",\"$out[2]\",$score\n";
}
close (F);
close (S);

# cforms.cha ¤ÎÆÉ¤ß¤³¤ß
my $ctype = "";
my @CTYPE;
open (F, "$PREFIX/cforms.cha") || die "Fatal: $PREFIX/cforms.cha cannot open\n";
while (<F>) {
    chomp;
    next if (/^;/ || /^$/ || /¸?´´/);
    if (/^\((\S+)\s*$/) {
	$ctype = $1;
    } elsif (/^\)\s*$/) {
	$ctype = "";
    } elsif ($ctype ne "" && /^\s+\(([^\)]+)\)/) {
	my ($a,$b,$c) = split /\s+/, $1;
	push @{$CTYPE{$ctype}}, [(&strip($a), &strip($b), &strip($c))];
    }
}
close (F);

# ¼­½ñ¤ÎÆÉ¤ß¤³¤ß
opendir (DICDIR, $PREFIX) || die "FATAL: $PREFIX cannot open\n";
my @dic = grep (/\.dic$/, readdir (DICDIR));
open (S, "> dic.sen") || die "FATAL: dic.txt cannot open\n";

for my $file (@dic) {
    print STDERR "$PREFIX/$file ...\n";
    open (F, "$PREFIX/$file") || die "FATAL: $PREFIX/$file cannot open\n";
    while (<F>) {
	chomp;
	my $lex; 

	if (/\(LEX \(([^ ]+) (\d+)/) {
	    $lex = $1;
	    $score = $2;
	}
	next if (! $lex);    

	my $read;
	my $pron;
	my $pos;
	my $ctype;

	$read  = $1 if (/\(READING ([^\)]+)/);
	$pron  = $1 if (/\(PRON ([^\)]+)/);
	$pos   = $1 if (/\(POS \(([^-\)]+)/);
	$ctype = $1 if (/\(CTYPE ([^-\)]+)/);

	my @posl = split / /, $pos;
	my $pos1 = $posl[0] || "*";
	my $pos2 = $posl[1] || "*";
	my $pos3 = $posl[2] || "*";
	my $pos4 = $posl[3] || "*";
	$ctype ||= "*";
	$pron  ||= $read;
	$base  = $lex;
	print S &conv ("$lex,$score,$pos1,$pos2,$pos3,$pos4,$ctype,*,$base,$read,$pron");
    }
    close (F);
}
close (S);
